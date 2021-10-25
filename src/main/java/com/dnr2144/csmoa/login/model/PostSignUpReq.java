package com.dnr2144.csmoa.login.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
public class PostSignUpReq {

    private String email;
    @Setter
    private String password;
    private String nickname;
}
