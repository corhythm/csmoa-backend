package com.dnr2144.csmoa.login.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostLoginRes {

    private final String xAccessToken;
    private final Long userId;

    @Builder
    public PostLoginRes(String xAccessToken, Long userId) {
        this.xAccessToken = xAccessToken;
        this.userId = userId;
    }
}
