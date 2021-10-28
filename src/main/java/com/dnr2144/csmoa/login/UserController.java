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
            return new BaseResponse<>(ex.getStatus());
        }

    }


}
