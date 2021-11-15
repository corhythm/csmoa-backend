package com.dnr2144.csmoa.login.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PostOAuthLoginReq {

    private String email;
    private String nickname;
    private String profileImageUrl;
    private String provider;
}
