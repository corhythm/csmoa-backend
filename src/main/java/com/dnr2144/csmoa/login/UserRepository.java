package com.dnr2144.csmoa.login;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.login.model.*;
import com.dnr2144.csmoa.login.query.UserSqlQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;


@Repository
@Slf4j
public class UserRepository {

    private JdbcTemplate jdbcTemplate;

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
                            .build(), postLoginReq.getEmail());

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // OAuth로 로그인하기
    public Long oAuthLogin(PostOAuthLoginReq postOAuthLoginReq) throws BaseException {

        // 이미 OAuth 계정으로 가입을 안 했다면
        if (checkExistsOAuthAccount(postOAuthLoginReq.getEmail(), postOAuthLoginReq.getProvider()) == 0) {
            String oauthSignUpUserQuery = UserSqlQuery.OAUTH_SIGN_UP_USER;
            jdbcTemplate.update(oauthSignUpUserQuery, postOAuthLoginReq.getEmail(),
                    postOAuthLoginReq.getNickname(), postOAuthLoginReq.getProvider(), postOAuthLoginReq.getProfileImageUrl());
        }

        String getUserId = "SELECT user_id from users where email = ? and provider = ?";
        return jdbcTemplate.queryForObject(getUserId, Long.class,
                postOAuthLoginReq.getEmail(), postOAuthLoginReq.getProvider());
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

    // 존재하는 계정인지 체크 (status == false 계정 포함)
    public Integer checkExistsEmail(String email) throws BaseException {
        try {
            String checkExistsEmailQuery = UserSqlQuery.CHECK_EXISTS_EMAIL;
            return jdbcTemplate.queryForObject(checkExistsEmailQuery, Integer.class, email);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 소셜 로그인으로 가입된 이력이 있는지 체크
    public Integer checkExistsOAuthAccount(String email, String provider) throws BaseException {
        try {
            String checkExistsOAuthAccountQuery = UserSqlQuery.CHECK_EXISTS_OAUTH_ACCOUNT;
            return jdbcTemplate.queryForObject(checkExistsOAuthAccountQuery, Integer.class, email, provider);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

}
