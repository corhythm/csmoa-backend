package com.dnr2144.csmoa.login.model;

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
