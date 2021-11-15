package com.dnr2144.csmoa.login;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.config.secret.Secret;
import com.dnr2144.csmoa.login.domain.*;
import com.dnr2144.csmoa.login.domain.model.CheckAccount;
import com.dnr2144.csmoa.util.AES128;
import com.dnr2144.csmoa.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public PostSignUpRes signUp(PostSignUpReq postSignUpReq) throws BaseException {

        // 빈 값인지 체크
        if (postSignUpReq.getEmail() == null || postSignUpReq.getNickname() == null
                || postSignUpReq.getPassword() == null || postSignUpReq.getEmail().isEmpty()
                || postSignUpReq.getNickname().isEmpty()
                || postSignUpReq.getPassword().isEmpty()) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        // 닉네임 중복체크
        if (validateDuplicateUserNickname(postSignUpReq.getNickname()) == 1) {
            throw new BaseException(BaseResponseStatus.NICKNAME_DUPLICATION_ERROR);
        }

        // 이메일 중복 체크
        if (validateDuplicateUserEmail(postSignUpReq.getEmail()) == 1) {
            throw new BaseException(BaseResponseStatus.EMAIL_DUPLICATION_ERROR);
        }

        // 내 생각에는 여기서 예외를 안 맞으면 더 상위 메소드로 가서 받는 듯
        // 비밀번호 암호화
        postSignUpReq.setPassword(encryptPassword(postSignUpReq.getPassword()));
        // 회원가입
        return userRepository.signUp(postSignUpReq);
    }

    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException {

        // 이메일, 비밀번호 값이 비었는지 체크
        if (postLoginReq.getEmail() == null || postLoginReq.getPassword() == null ||
                postLoginReq.getEmail().isEmpty() || postLoginReq.getPassword().isEmpty()) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        // 가입한 사용자인지 조회
        if (validateDuplicateUserEmail(postLoginReq.getEmail()) == 0) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }

        // 이메일을 갖고 동일한 계정 정보 가져오기(userId, password)
        CheckAccount checkAccount = userRepository.login(postLoginReq);

        // 비밀번호 복호화
        log.info("암호화된 비밀번호: " + checkAccount.getPassword());
        checkAccount.setPassword(decryptPassword(checkAccount.getPassword()));
        log.info("복호화된 비밀번호: " + postLoginReq.getPassword());

        // 비밀번호가 일치하면
        if (postLoginReq.getPassword().equals(checkAccount.getPassword())) {
            log.error("decrypt 전");
            return PostLoginRes.builder()
                    .userId(checkAccount.getUserId())
                    .accessToken(jwtService.createJwt(checkAccount.getUserId(), jwtService.ACCESS_TOKEN))
                    .refreshToken(jwtService.createJwt(checkAccount.getUserId(), jwtService.REFRESH_TOKEN))
                    .build();
        } else {
            // 비밀번호 불일치
            throw new BaseException(BaseResponseStatus.UNMATCHED_PASSWORD_ERROR);
        }
    }

    public PostLoginRes oAuthLogin(PostOAuthLoginReq postOAuthLoginReq) throws BaseException {
        // 필수 확인 정보 다 기입됐는지 확인
        if (postOAuthLoginReq.getEmail() == null || postOAuthLoginReq.getNickname() == null ||
                postOAuthLoginReq.getProvider() == null || postOAuthLoginReq.getEmail().isEmpty() ||
                postOAuthLoginReq.getNickname().isEmpty() || postOAuthLoginReq.getProvider().isEmpty()) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        // 허용된 OAuth 공급자인지 확인
        switch (postOAuthLoginReq.getProvider()) {
            case "google":
            case "kakao":
                break;
            default:
                throw new BaseException(BaseResponseStatus.INVALID_OAUTH_PROVIDER);
        }

        // Local로 이미 동일 이메일로 가입했는지 확인
        if (userRepository.checkLocalProviderEmailExits(postOAuthLoginReq.getEmail()) == 1) {
            throw new BaseException(BaseResponseStatus.EMAIL_DUPLICATION_ERROR);
        }

        Long userId = userRepository.oAuthLogin(postOAuthLoginReq);
        PostLoginRes postLoginRes = PostLoginRes.builder()
                .userId(userId)
                .accessToken(jwtService.createJwt(userId, jwtService.ACCESS_TOKEN))
                .refreshToken(jwtService.createJwt(userId, jwtService.REFRESH_TOKEN))
                .build();
        log.info("postLoginRes = " + postLoginRes.toString());
        return postLoginRes;
    }

    // 유저 정보 가져오기
    public GetUserInfoRes getUserInfo(long userId) throws BaseException {
        if (userRepository.checkUserExists(userId) == 0) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }

        return userRepository.getUserInfo(userId);
    }

    // 유저 정보 변경
    public PatchUserInfoRes patchUserInfo(long userId, PatchUserInfoReq patchUserInfoReq) throws BaseException {

        // 존재하지 않는 유저일 때
        if (userRepository.checkUserExists(userId) == 0) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }

        // patchUserInfoReq 자체가 null이거나 nickname이랑 이미지 파일 두 개다 null일 때
        if (patchUserInfoReq == null ||
                (patchUserInfoReq.getProfileImageFile() == null && patchUserInfoReq.getNickname() == null)) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        // 여기서 파이어베이스 스토리지에 업로드하자 -> fileName: csmoa_userId.jpg

        return userRepository.patchUserInfo(userId, patchUserInfoReq);
    }

    // 비밀번호 암호화
    private String encryptPassword(String plainPassword) throws BaseException {

        try {
            return new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(plainPassword);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.FAILED_TO_PASSWORD_ENCRYPT);
        }
    }

    // 비밀번호 복호화
    private String decryptPassword(String encryptedPassword) throws BaseException {
        try {
            return new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(encryptedPassword);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.FAILED_TO_PASSWORD_DECRYPT);
        }
    }

    public int validateDuplicateUserEmail(String email) throws BaseException {
        return userRepository.validateUserEmail(email);
    }

    // 닉네임 중복 체크
    public int validateDuplicateUserNickname(String nickname) throws BaseException {
        return userRepository.validateUserNickname(nickname);
    }

}
