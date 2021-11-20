package com.dnr2144.csmoa.review;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponse;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.login.domain.PatchUserInfoRes;
import com.dnr2144.csmoa.review.domain.PostReviewReq;
import com.dnr2144.csmoa.review.domain.PostReviewRes;
import com.dnr2144.csmoa.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final JwtService jwtService;
    private final ReviewService reviewService;

    @PostMapping("/reviews")
    @ResponseBody
    public BaseResponse<PostReviewRes> postReview(@RequestHeader("Access-Token") String accessToken,
                                            PostReviewReq postReviewReq) {
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }

        log.info(postReviewReq.toString());

        try {
            long userId = jwtService.getUserId(accessToken);
            PostReviewRes postReviewRes = reviewService.postReview(userId, postReviewReq);
            log.info("postReviewRes = " + postReviewRes.toString());
            return new BaseResponse<>(postReviewRes);
        } catch (BaseException ex) {
            ex.printStackTrace();
            log.error("(/reviews): " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }




}





