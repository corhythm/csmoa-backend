package com.dnr2144.csmoa.login.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@ToString
@RequiredArgsConstructor
public class PatchUserInfoReq {
    private final String nickname;
    private final MultipartFile profileImageFile;
}
