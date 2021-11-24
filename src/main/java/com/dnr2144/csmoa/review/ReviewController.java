package com.dnr2144.csmoa.review;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponse;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.review.domain.PostReviewReq;
import com.dnr2144.csmoa.review.domain.PostReviewRes;
import com.dnr2144.csmoa.review.domain.model.Comment;
import com.dnr2144.csmoa.review.domain.model.DetailedReview;
import com.dnr2144.csmoa.review.domain.model.Review;
import com.dnr2144.csmoa.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@ResponseBody
public class ReviewController {

    private final JwtService jwtService;
    private final ReviewService reviewService;

    @PostMapping("/reviews")
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
            log.error("((POST) /reviews): " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

    @GetMapping("/best-reviews")
    public BaseResponse<List<Review>> getReviews(@RequestHeader("Access-Token") String accessToken) {
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            log.info("/best-reviews / userId = " + userId);
            List<Review> bestReviews = reviewService.getBestReviews(userId);
            log.info("bestReviews.size = " + bestReviews.size() + ", bestReviews = " + bestReviews.toString());
            return new BaseResponse<>(bestReviews);
        } catch (BaseException ex) {
            ex.printStackTrace();
            log.error("((GET) /best-reviews): " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

    @GetMapping("/reviews")
    public BaseResponse<List<Review>> getReviews(@RequestHeader("Access-Token") String accessToken,
                                                 @RequestParam Integer page) {
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            log.info("/reviews / userId = " + userId + ", page = " + page);
            List<Review> reviews = reviewService.getReviews(userId, page);
//            log.info("getReviewRes = " + getReviewsRes.toString());
            return new BaseResponse<>(reviews);
        } catch (BaseException ex) {
            ex.printStackTrace();
            log.error("((GET) /reviews): " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

    @GetMapping("reviews/{reviewId}")
    public BaseResponse<DetailedReview> getDetailedReview(@PathVariable("reviewId") Long reviewId,
                                                          @RequestHeader("Access-Token") String accessToken) {
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            log.info("/detailedReview / userId = " + userId);
            DetailedReview detailedReview = reviewService.getDetailedReview(reviewId, userId);
            log.info("detailedReview = " + detailedReview.toString());
            return new BaseResponse<>(detailedReview);
        } catch (BaseException ex) {
            ex.printStackTrace();
            log.error("((GET) /detailedReview): " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

    @GetMapping("/reviews/{reviewId}/comments")
    public BaseResponse<List<Comment>> getComments(@PathVariable("reviewId") Long reviewId,
                                                   @RequestParam("page") Integer pageNum) {
        try {
            List<Comment> comments = reviewService.getComments(reviewId, pageNum);
            log.info("comments = " + comments.toString());
            return new BaseResponse<>(comments);
        } catch (BaseException ex) {
            ex.printStackTrace();
            log.error("((GET) /getComments): " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }


}





