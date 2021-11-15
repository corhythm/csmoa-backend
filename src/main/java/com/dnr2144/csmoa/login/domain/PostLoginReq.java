package com.dnr2144.csmoa.login.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PostLoginReq {

    private String email;
    @Setter
    private String password;
}
