package com.dnr2144.csmoa.config;

import lombok.Getter;

/*
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {

    SUCCESS(true, 100, "요청에 성공하였습니다."),

    /**
     * 200 : Get 오류
     */
    // Common
    REQUEST_ERROR(false, 200, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 201, "JWT를 입력해주세요."),
    INVALID_JWT(false, 202, "유효하지 않은 JWT입니다."),
    FAILED_TO_PASSWORD_ENCRYPT(false, 203, "비밀번호 암호화에 실패했습니다"),
    FAILED_TO_PASSWORD_DECRYPT(false, 203, "비밀번호 복호화에 실패했습니다"),

    // 210번 대: user 오류
    NICKNAME_DUPLICATION_ERROR(false, 210, "이미 존재하는 닉네임입니다"),
    EMAIL_DUPLICATION_ERROR(false, 211, "이미 존재하는 계정입니다."),
    INVALID_ACCOUNT_ERROR(false, 212, "계정이 존재하지 않거나 사용이 정지됐습니다."),
    UNMATCHED_PASSWORD_ERROR(false, 213, "비밀번호가 일치하지 않습니다."),
    INVALID_OAUTH_PROVIDER(false, 214, "허용되지 않은 OAUTH 공급자입니다."),
    FAILED_TO_UPLOAD_USER_PROFILE_IMAGE(false, 215, "프로필 이미지를 업로드하는 데 실패했습니다."),

    // 220번 대: 행사 상품 오류
    INVALID_EVENT_ITEM_ERROR(false, 220, "존재하지 않는 행사상품입니다."),


    // 230번 대: 리뷰 에러
    INVALID_REVIEW_ERROR(false, 230, "존재하지 않는 리뷰입니다"),
    EMPTY_PARENT_COMMENT(false, 231, "대댓글을 달고자 하는 해당 리뷰에 부모 댓글이 존재하지 않습니다."),
    /**
     * 400 : Database, Server 오류
     */
    DATABASE_ERROR(false, 400, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 401, "서버와의 연결에 실패하였습니다."),

    PASSWORD_ENCRYPTION_ERROR(false, 411, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 412, "비밀번호 복호화에 실패하였습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
