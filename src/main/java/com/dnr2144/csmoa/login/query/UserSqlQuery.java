package com.dnr2144.csmoa.login.query;

public class UserSqlQuery {

    public static final String VALIDATE_DUPLICATE_USER_EMAIL = "SELECT EXISTS(SELECT * FROM users WHERE email = ?);";

    public static final String VALIDATE_DUPLICATE_USER_NICKNAME = "SELECT EXISTS(SELECT * FROM users WHERE nickname = ?);";

    public static final String SIGN_UP_USER = "INSERT INTO users (email, nickname, password, provider) VALUE (?, ?, ?, ?);";

    public static final String SUCCESSFUL_SIGN_UP = "SELECT user_id, email, nickname, provider FROM users WHERE email = ?;";
}
