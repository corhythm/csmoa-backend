package com.dnr2144.csmoa.login;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponse;
import com.dnr2144.csmoa.config.BaseResponseStatus;

import com.dnr2144.csmoa.login.model.PostLoginReq;
import com.dnr2144.csmoa.login.model.PostLoginRes;
import com.dnr2144.csmoa.login.model.PostSignUpReq;
import com.dnr2144.csmoa.login.model.PostSignUpRes;
import com.dnr2144.csmoa.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    @ResponseBody
    public BaseResponse<PostSignUpRes> signUp(@RequestBody PostSignUpReq postSignUpReq) {

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
        if (postLoginReq == null) {
            return new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR);
        }

        try {
            PostLoginRes postLoginRes = userService.login(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException ex) {
            return new BaseResponse<>(ex.getStatus());
        }
    }


}
