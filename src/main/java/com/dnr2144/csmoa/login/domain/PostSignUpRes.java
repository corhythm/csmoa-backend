package com.dnr2144.csmoa.login.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostSignUpRes {
    private final Long userId;
    private final String email;
    private final String nickname;
    private final String provider;

    @Builder
    public PostSignUpRes(Long userId, String email, String nickname, String provider) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
    }
}
