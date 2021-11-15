package com.dnr2144.csmoa.login.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
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
