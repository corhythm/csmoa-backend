package com.dnr2144.csmoa.login;

public class UserSqlQuery {

    public static final String VALIDATE_DUPLICATE_USER_EMAIL = "SELECT EXISTS(SELECT * FROM users WHERE email = ?);";

    public static final String VALIDATE_DUPLICATE_USER_NICKNAME = "SELECT EXISTS(SELECT * FROM users WHERE nickname = ?);";

    public static final String SIGN_UP_USER = "INSERT INTO users (email, nickname, password, provider) VALUE (?, ?, ?, ?);";

    public static final String SUCCESSFUL_SIGN_UP = "SELECT user_id, email, nickname, provider FROM users WHERE email = ?;";

    public static final String LOGIN_USER = "SELECT user_id, password FROM users WHERE email = ? AND provider = 'local';";

    public static final String OAUTH_SIGN_UP_USER = "INSERT INTO users (email, nickname, provider, profile_image_url) VALUE (?, ?, ?, ?);";

    public static final String NOT_OAUTH_USER_EMAIL_EXISTS =  "SELECT EXISTS(SELECT * FROM users WHERE email = ? AND provider = 'local');";

    public static final String OAUTH_USER_EMAIL_EXISTS = "SELECT EXISTS(SELECT * FROM users WHERE email = ? AND provider = ?);";

    public static final String CHECK_USER_EXISTS = "SELECT EXISTS(SELECT * FROM users WHERE user_id = ?);";

    public static final String GET_USER_INFO = "SELECT user_id, email, nickname, profile_image_url FROM users WHERE user_id = ?;";

    public static final String PATCH_USER_INFO_ALL = "UPDATE users SET nickname = ?, profile_image_url = ? WHERE user_id = ?;";

    public static final String PATCH_USER_INFO_ONLY_PROFILE_IMAGE = "UPDATE users SET profile_image_url = ? WHERE user_id = ?;";

    public static final String PATCH_USER_INFO_ONLY_NICKNAME = "UPDATE users SET nickname = ? WHERE user_id = ?;";

    public static final String GET_UPDATED_USER_INFO = "SELECT user_id, nickname, profile_image_url FROM users WHERE user_id = ?;";
}
