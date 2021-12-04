package com.dnr2144.csmoa.review;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponse;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.review.domain.PostReviewLikeRes;
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

    // NOTE: 리뷰 쓰기
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

    // NOTE: 베스트 리뷰 가져오기
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

    // NOTE: 일반 리뷰 가져오기
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

    // NOTE: 리뷰 세부정보 가져오기
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

    // NOTE: 부모 댓글 가져오기
    @GetMapping("/reviews/{reviewId}/comments")
    public BaseResponse<List<Comment>> getParentComments(@PathVariable("reviewId") Long reviewId,
                                                         @RequestParam("page") Integer pageNum) {
        try {
            List<Comment> parentComments = reviewService.getParentComments(reviewId, pageNum);
            log.info("parentComments.size = " + parentComments.size() + ", getParentComments = " + parentComments.toString());
            return new BaseResponse<>(parentComments);
        } catch (BaseException ex) {
            ex.printStackTrace();
            log.error("((GET) /getComments): " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

    // NOTE: 부모 댓글 쓰기
    @PostMapping("/reviews/{reviewId}/comments")
    public BaseResponse<Comment> postParentComment(@PathVariable("reviewId") Long reviewId,
                                                   @RequestHeader("Access-Token") String accessToken,
                                                   @RequestBody String content) {
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            Comment myParentComment = reviewService.postParentComment(reviewId, userId, content);
            log.info("myParentComment = " + myParentComment.toString());
            return new BaseResponse<>(myParentComment);
        } catch (BaseException ex) {
            ex.printStackTrace();
            log.error("postParentReview): " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

    // NOTE: 자식 댓글 가져오기
    @GetMapping("/comments/{bundleId}/child-comments")
    public BaseResponse<List<Comment>> getChildComments(@PathVariable("bundleId") Long bundleId,
                                                        @RequestParam("page") Integer pageNum) {
        try {
            List<Comment> childComments = reviewService.getChildComments(bundleId, pageNum);
            log.info("childComments.size = " + childComments.size() + ", getChildComments = " + childComments.toString());
            return new BaseResponse<>(childComments);
        } catch (BaseException ex) {
            ex.printStackTrace();
            log.error("((GET) /getChildComments): " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

    // NOTE: 자식 댓글 쓰기
    @PostMapping("/reviews/{reviewId}/comments/{bundleId}/child-comments")
    public BaseResponse<Comment> postChildComment(@PathVariable("reviewId") Long reviewId,
                                                  @PathVariable("bundleId") Long bundleId,
                                                  @RequestHeader("Access-Token") String accessToken,
                                                  @RequestBody String content
    ) {
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            Comment myChildComment = reviewService.postChildComment(reviewId, bundleId, userId, content);
            log.info("myChildComment = " + myChildComment.toString());
            return new BaseResponse<>(myChildComment);
        } catch (BaseException ex) {
            ex.printStackTrace();
            log.error("postChildComment): " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

    // NOTE: 리뷰 좋아요 / 좋아요 취소
    @PostMapping("/reviews/{reviewId}/like")
    public BaseResponse<PostReviewLikeRes> postReviewLike(@PathVariable("reviewId") Long reviewId,
                                                  @RequestHeader("Access-Token") String accessToken) {
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            log.info("reviewId = " + reviewId + ", userId = " + userId);
            PostReviewLikeRes postReviewLikeRes = reviewService.postReviewLike(reviewId, userId);
            log.info("postReviewLikeRes = " + postReviewLikeRes.toString());
            return new BaseResponse<>(postReviewLikeRes);
        } catch (BaseException ex) {
            ex.printStackTrace();
            log.error("postReviewLike: " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

}





