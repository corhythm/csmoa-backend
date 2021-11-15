package com.dnr2144.csmoa.login.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostTokenRes {

    private final Long userId;
    private final String accessToken;
    private final String refreshToken;

    @Builder
    public PostTokenRes(Long userId, String accessToken, String refreshToken) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
