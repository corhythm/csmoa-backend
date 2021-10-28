package com.dnr2144.csmoa.login.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class PostLoginRes {

    private final String accessToken;
    private final Long userId;

    @Builder
    public PostLoginRes(String accessToken, Long userId) {
        this.accessToken = accessToken;
        this.userId = userId;
    }
}
