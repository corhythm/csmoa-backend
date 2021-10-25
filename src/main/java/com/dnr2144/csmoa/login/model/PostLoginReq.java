package com.dnr2144.csmoa.login.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
public class PostLoginReq {

    private String email;
    @Setter
    private String password;
}
