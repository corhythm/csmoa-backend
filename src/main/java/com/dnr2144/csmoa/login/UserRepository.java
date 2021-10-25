package com.dnr2144.csmoa.login;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.login.model.PostSignUpReq;
import com.dnr2144.csmoa.login.model.PostSignUpRes;
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
    public void setDataSource(DataSource dataSource) { this.jdbcTemplate = new JdbcTemplate(dataSource); }

    // 회원가입
    public PostSignUpRes signUp(PostSignUpReq postSignUpReq) throws BaseException {

        if (postSignUpReq.getEmail() == null || postSignUpReq.getNickname() == null || postSignUpReq.getPassword() == null) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        try {
            String signUpUserQuery = UserSqlQuery.SIGN_UP_USER;
            // email, nickname, password, provider
            jdbcTemplate.update(signUpUserQuery, postSignUpReq.getEmail(), postSignUpReq.getNickname(), postSignUpReq.getPassword(), "local");

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

}
