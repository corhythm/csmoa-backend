package com.dnr2144.csmoa.login;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponse;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.login.model.*;
import com.dnr2144.csmoa.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    @ResponseBody
    public BaseResponse<PostSignUpRes> signUp(@RequestBody PostSignUpReq postSignUpReq) {

        log.info("/signup");
        if (postSignUpReq == null) {
            return new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR);
        }

        try {
            PostSignUpRes postSignUpRes = userService.signUp(postSignUpReq);
            return new BaseResponse<>(postSignUpRes);
        } catch (BaseException ex) {
            return new BaseResponse<>(ex.getStatus());
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq) {
        log.info("/login");
        if (postLoginReq == null) {
            return new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR);
        }

        try {
            PostLoginRes postLoginRes = userService.login(postLoginReq);
            log.info(postLoginRes.toString());
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException ex) {
            return new BaseResponse<>(ex.getStatus());
        }
    }

    @PostMapping("/login/oauth")
    @ResponseBody
    public BaseResponse<PostLoginRes> oAuthLogin(@RequestBody PostOAuthLoginReq postOAuthLoginReq) {

        log.info("/login/oauth");

        if (postOAuthLoginReq == null) {
            return new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR);
        }

        log.info("postOAuthLoginReq = " + postOAuthLoginReq.toString());

        try {
            PostLoginRes postLoginRes = userService.oAuthLogin(postOAuthLoginReq);
            log.info(postLoginRes.toString());
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException ex) {
            log.error("login/oauth: " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

    // accessToken과 refreshToken 재발급
    @GetMapping("/token")
    @ResponseBody
    public BaseResponse<PostTokenRes> getRefreshJwtToken(@RequestHeader("Refresh-Token") String refreshToken) {

        if (refreshToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }

        try {
            long userId = jwtService.getUserId(refreshToken);

            // token expiration refresh
            PostTokenRes postTokenRes = PostTokenRes.builder()
                    .accessToken(jwtService.createJwt(userId, jwtService.ACCESS_TOKEN))
                    .refreshToken(jwtService.createJwt(userId, jwtService.REFRESH_TOKEN))
                    .userId(userId)
                    .build();
            return new BaseResponse<>(postTokenRes);
        } catch (BaseException ex) {
            log.error("token: " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

    @GetMapping("/user-info")
    @ResponseBody
    public BaseResponse<GetUserInfoRes> getUserInfo(@RequestHeader("Access-Token") String accessToken) {

        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }

        try {
            long userId = jwtService.getUserId(accessToken);

            GetUserInfoRes getUserInfoRes = userService.getUserInfo(userId);
            log.info("getUserInfoRes = " + getUserInfoRes.toString());
            return new BaseResponse<>(getUserInfoRes);

        } catch (BaseException ex) {
            log.error("(get)user-info: " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

    // 유정 정보 수정
    @PatchMapping("/user-info")
    @ResponseBody
    public BaseResponse<PatchUserInfoRes> patchUserInfo(@RequestHeader("Access-Token") String accessToken, PatchUserInfoReq patchUserInfoReq) {

        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }

        log.info(patchUserInfoReq.toString());

        try {
            long userId = jwtService.getUserId(accessToken);

            PatchUserInfoRes patchUserInfoRes = userService.patchUserInfo(userId, patchUserInfoReq);

            return new BaseResponse<>(patchUserInfoRes);
        } catch (BaseException ex) {
            log.error("(patch)user-info: " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }



    }


}
