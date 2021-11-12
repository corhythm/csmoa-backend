package com.dnr2144.csmoa.login.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PatchUserInfoRes {
    private final Long userId;
    private final String nickname;
    private final String userProfileImageUrl;

    @Builder
    public PatchUserInfoRes(Long userId, String nickname, String userProfileImageUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.userProfileImageUrl = userProfileImageUrl;
    }
}
