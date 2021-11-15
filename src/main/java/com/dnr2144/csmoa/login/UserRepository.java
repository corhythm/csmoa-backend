package com.dnr2144.csmoa.login;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.firebase.FirebaseStorageManager;
import com.dnr2144.csmoa.login.domain.*;
import com.dnr2144.csmoa.login.domain.model.CheckAccount;
import com.dnr2144.csmoa.login.query.UserSqlQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {

    private JdbcTemplate jdbcTemplate;
    private final FirebaseStorageManager firebaseStorageManager;


    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 회원가입
    public PostSignUpRes signUp(PostSignUpReq postSignUpReq) throws BaseException {

        try {
            String signUpUserQuery = UserSqlQuery.SIGN_UP_USER;
            // email, nickname, password, provider
            jdbcTemplate.update(signUpUserQuery, postSignUpReq.getEmail(),
                    postSignUpReq.getNickname(), postSignUpReq.getPassword(), "local");

            String successfulSignUp = UserSqlQuery.SUCCESSFUL_SIGN_UP;
            return jdbcTemplate.queryForObject(successfulSignUp,
                    (rs, rowNum) -> PostSignUpRes.builder()
                            .email(rs.getString("email"))
                            .userId(rs.getLong("user_id"))
                            .nickname(rs.getString("nickname"))
                            .provider(rs.getString("provider"))
                            .build(), postSignUpReq.getEmail());

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 로그인
    public CheckAccount login(PostLoginReq postLoginReq) throws BaseException {

        try {
            String loginUserQuery = UserSqlQuery.LOGIN_USER;
            return jdbcTemplate.queryForObject(loginUserQuery,
                    (rs, rowNum) -> CheckAccount.builder()
                            .userId(rs.getLong("user_id"))
                            .password(rs.getString("password"))
                            .build(),
                    postLoginReq.getEmail());

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // OAuth로 로그인하기
    public Long oAuthLogin(PostOAuthLoginReq postOAuthLoginReq) throws BaseException {
        try {
            if (checkOAuthEmailExists(postOAuthLoginReq.getEmail(), postOAuthLoginReq.getProvider()) == 0) {
                String oauthSignUpUserQuery = UserSqlQuery.OAUTH_SIGN_UP_USER;
                jdbcTemplate.update(oauthSignUpUserQuery, postOAuthLoginReq.getEmail(),
                        postOAuthLoginReq.getNickname(), postOAuthLoginReq.getProvider(), postOAuthLoginReq.getProfileImageUrl());
            }

            String getUserIdQuery = "SELECT user_id FROM users WHERE email = ? AND provider = ?";
            return jdbcTemplate.queryForObject(getUserIdQuery, Long.class,
                    postOAuthLoginReq.getEmail(), postOAuthLoginReq.getProvider());
        } catch (Exception exception) {
            log.error("oAuthLogin / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetUserInfoRes getUserInfo(long userId) throws BaseException {
        try {
            String getUserInfoQuery = UserSqlQuery.GET_USER_INFO;
            return jdbcTemplate.queryForObject(getUserInfoQuery,
                    (rs, rowNum) -> GetUserInfoRes.builder()
                            .userId(rs.getLong("user_id"))
                            .email(rs.getString("email"))
                            .nickname(rs.getString("nickname"))
                            .userProfileImageUrl(rs.getString("profile_image_url"))
                            .build()
                    , userId);

        } catch (Exception exception) {
            log.error("getUserInfo / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 프로필 정보 변경
    public PatchUserInfoRes patchUserInfo(long userId, PatchUserInfoReq patchUserInfoReq) throws BaseException {

        try {
            if (patchUserInfoReq.getNickname() != null && patchUserInfoReq.getProfileImageFile() != null) { // 닉네임 & 프로필 이미지 변경
                if (validateUserNickname(patchUserInfoReq.getNickname()) == 1) { // 닉네임 중복 체크
                    log.info("닉네임 프로필 이미지 둘 다 변경 / patchUserInfoReq = " + patchUserInfoReq);
                    throw new BaseException(BaseResponseStatus.NICKNAME_DUPLICATION_ERROR);
                };
                String absoluteFileUrl = firebaseStorageManager.saveProfileImage(userId, patchUserInfoReq.getProfileImageFile());
                jdbcTemplate.update(UserSqlQuery.PATCH_USER_INFO_ALL, patchUserInfoReq.getNickname(), absoluteFileUrl, userId);

            } else if (patchUserInfoReq.getNickname() == null && patchUserInfoReq.getProfileImageFile() != null) { // 프로필 이미지만 변경
                log.info("프로필 이미지만 변경 / patchUserInfoReq = " + patchUserInfoReq);
                String absoluteFileUrl = firebaseStorageManager.saveProfileImage(userId, patchUserInfoReq.getProfileImageFile());
                jdbcTemplate.update(UserSqlQuery.PATCH_USER_INFO_ONLY_PROFILE_IMAGE, absoluteFileUrl, userId);

            } else { // 닉네임만 변경
                log.info("닉네임만 변경/ patchUserInfoReq = " + patchUserInfoReq);
                if (validateUserNickname(patchUserInfoReq.getNickname()) == 1) {
                    throw new BaseException(BaseResponseStatus.NICKNAME_DUPLICATION_ERROR);
                };
                jdbcTemplate.update(UserSqlQuery.PATCH_USER_INFO_ONLY_NICKNAME, patchUserInfoReq.getNickname(), userId);
            }

            // 업데이트된 정보 가져오기
            return jdbcTemplate.queryForObject(UserSqlQuery.GET_UPDATED_USER_INFO,
                    (rs, rowNum) -> PatchUserInfoRes.builder()
                            .userId(rs.getLong("user_id"))
                            .nickname(rs.getString("nickname"))
                            .userProfileImageUrl(rs.getString("profile_image_url"))
                            .build(), userId);

        } catch (Exception exception) {
            if (exception instanceof BaseException) {
                throw new BaseException(((BaseException) exception).getStatus());
            }
            exception.printStackTrace();
            log.error("patchUserInfo / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 이메일 중복 체크
    public Integer validateUserEmail(String email) throws BaseException {

        try {
            String validateUserEmailQuery = UserSqlQuery.VALIDATE_DUPLICATE_USER_EMAIL;
            return jdbcTemplate.queryForObject(validateUserEmailQuery, Integer.class, email);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 닉네임 중복체크
    public Integer validateUserNickname(String nickname) throws BaseException {
        try {
            String validateUserNicknameQuery = UserSqlQuery.VALIDATE_DUPLICATE_USER_NICKNAME;
            return jdbcTemplate.queryForObject(validateUserNicknameQuery, Integer.class, nickname);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // OAuth는 아닌데 동일 이메일로 local로 가입한 경우
    public Integer checkLocalProviderEmailExits(String email) throws BaseException {
        try {
            String notOAuthEmailExitsQuery = UserSqlQuery.NOT_OAUTH_USER_EMAIL_EXISTS;
            return jdbcTemplate.queryForObject(notOAuthEmailExitsQuery, Integer.class, email);
        } catch (Exception exception) {
            log.error("checkLocalProviderEmailExits / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // OAuth로 이미 계정 정보가 DB에 들어가 있는지 체크
    public Integer checkOAuthEmailExists(String email, String provider) throws BaseException {
        try {
            String oAUthUserEmailExistsQuery = UserSqlQuery.OAUTH_USER_EMAIL_EXISTS;
            return jdbcTemplate.queryForObject(oAUthUserEmailExistsQuery, Integer.class, email, provider);
        } catch (Exception exception) {
            log.error("checkOAuthEmailExists / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public Integer checkUserExists(long userId) throws BaseException {
        try {
            String checkUserExistsQuery = UserSqlQuery.CHECK_USER_EXISTS;
            return jdbcTemplate.queryForObject(checkUserExistsQuery, Integer.class, userId);
        } catch (Exception exception) {
            log.error("checkUserExists / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }




}
