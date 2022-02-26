package com.dnr2144.csmoa.login.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GetUserInfoRes {
    private final Long userId;
    private final String email;
    private final String nickname;
    private final String userProfileImageUrl;

    @Builder
    public GetUserInfoRes(Long userId, String email, String nickname, String userProfileImageUrl) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.userProfileImageUrl = userProfileImageUrl;
    }
}
