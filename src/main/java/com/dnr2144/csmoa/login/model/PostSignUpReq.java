package com.dnr2144.csmoa.login.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class PostSignUpReq {

    private final String email;
    @Setter
    private String password;
    private final String nickname;
}
