package com.dnr2144.csmoa.login;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.config.secret.Secret;
import com.dnr2144.csmoa.login.model.PostSignUpReq;
import com.dnr2144.csmoa.login.model.PostSignUpRes;
import com.dnr2144.csmoa.util.AES128;
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

    public PostSignUpRes signUp(PostSignUpReq postSignUpReq) throws BaseException {

        // 중복체크
        if (validateDuplicateUserNickname(postSignUpReq.getNickname()) == 1) {
            throw new BaseException(BaseResponseStatus.NICKNAME_DUPLICATION_ERROR);
        }

        if (validateDuplicateUserEmail(postSignUpReq.getEmail()) == 1) {
            throw new BaseException(BaseResponseStatus.EMAIL_DUPLICATION_ERROR);
        }

        // password encrypt
        String encryptedPassword = "";

        try {
            encryptedPassword = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postSignUpReq.getPassword());
            postSignUpReq.setPassword(encryptedPassword);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            log.error(e.getMessage());
        }

        return userRepository.signUp(postSignUpReq);
    }


    public int validateDuplicateUserEmail(String email) throws BaseException {

        if (email == null || email.isEmpty()) {
            log.error(BaseResponseStatus.REQUEST_ERROR.toString());
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        return userRepository.validateUserEmail(email);
    }

    public int validateDuplicateUserNickname(String nickname) throws BaseException {
        if (nickname == null || nickname.isEmpty()) {
            log.error(BaseResponseStatus.REQUEST_ERROR.toString());
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        return userRepository.validateUserNickname(nickname);
    }

}
