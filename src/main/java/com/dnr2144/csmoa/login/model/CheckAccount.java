package com.dnr2144.csmoa.login.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// 로그인할 때 계정과 비밀번호 일치 여부 검사할 DTO
// 비밀번호 일치하면 userId랑 JWT 토큰 발급
@Getter
public class CheckAccount {

    private final Long userId;

    @Setter
    private String password;

    @Builder
    public CheckAccount(Long userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
