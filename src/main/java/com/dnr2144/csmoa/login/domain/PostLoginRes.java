package com.dnr2144.csmoa.login.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PostLoginRes {

    private final String accessToken;
    private final String refreshToken;
    private final Long userId;

    @Builder
    public PostLoginRes(String accessToken, String refreshToken, Long userId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
