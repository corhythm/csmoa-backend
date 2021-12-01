package com.dnr2144.csmoa.review;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.login.UserRepository;
import com.dnr2144.csmoa.review.domain.PostReviewLikeRes;
import com.dnr2144.csmoa.review.domain.PostReviewReq;
import com.dnr2144.csmoa.review.domain.PostReviewRes;
import com.dnr2144.csmoa.review.domain.model.Comment;
import com.dnr2144.csmoa.review.domain.model.DetailedReview;
import com.dnr2144.csmoa.review.domain.model.Review;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostReviewRes postReview(long userId, PostReviewReq postReviewReq) throws BaseException {

        // 존재하지 않는 유저일 때
        if (userRepository.checkUserExists(userId) == 0) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }

        // null 체크
        if (postReviewReq == null || postReviewReq.getReviewImages() == null || postReviewReq.getTitle() == null ||
                postReviewReq.getPrice() == null || postReviewReq.getRating() == null || postReviewReq.getCategory() == null ||
                postReviewReq.getCsBrand() == null || postReviewReq.getContent() == null) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        return reviewRepository.postReview(userId, postReviewReq);
    }

    @Transactional
    public List<Review> getBestReviews(Long userId) throws BaseException {
        if (userId == null) { // 입력값 null 체크
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        // 존재하지 않는 유저일 때
        if (userRepository.checkUserExists(userId) == 0) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        return reviewRepository.getBestReviews(userId);
    }

    @Transactional
    public List<Review> getReviews(Long userId, Integer pageNum) throws BaseException {
        if (userId == null || pageNum == null || pageNum < 1) { // 입력값 null 체크
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        if (userRepository.checkUserExists(userId) == 0) { // 존재하지 않는 유저일 때
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        return reviewRepository.getReviews(userId, pageNum);
    }

    @Transactional
    public DetailedReview getDetailedReview(Long reviewId, Long userId) throws BaseException {
        if (reviewId == null || userId == null || reviewId < 1 || userId < 1) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        if (userRepository.checkUserExists(userId) == 0) { // 존재하지 않는 유저일 때
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        return reviewRepository.getDetailedReview(reviewId, userId);
    }

    @Transactional
    public List<Comment> getParentComments(Long reviewId, Integer pageNum) throws BaseException {
        if (reviewId == null || pageNum == null || reviewId < 1 || pageNum < 1) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        return reviewRepository.getParentComments(reviewId, pageNum);
    }

    @Transactional
    public Comment postParentComment(Long reviewId, Long userId, String content) throws BaseException {
        if (reviewId == null || userId == null || content == null || reviewId < 1 || userId < 1 || content.isEmpty()) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        if (userRepository.checkUserExists(userId) == 0) { // 존재하지 않는 유저일 때
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        return reviewRepository.postParentComment(reviewId, userId, content);
    }


    // NOTE: 자식 댓글 가져오기
    @Transactional
    public List<Comment> getChildComments(Long bundleId, Integer pageNum) throws BaseException {
        if (bundleId == null || pageNum == null || bundleId < 1 || pageNum < 1) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        return reviewRepository.getChildComments(bundleId, pageNum);
    }

    // NOTE: 자식 댓글 쓰기
    @Transactional
    public Comment postChildComment(Long reviewId, Long bundleId, Long userId, String content) throws BaseException {
        if (reviewId == null || bundleId == null || userId == null || content == null ||
                reviewId < 1 || bundleId < 1 || userId < 1 || content.length() < 1) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        if (userRepository.checkUserExists(userId) == 0) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        if (reviewRepository.checkExistsParentCommentInThatReview(reviewId, bundleId) == 0) {
            throw new BaseException(BaseResponseStatus.EMPTY_PARENT_COMMENT);
        }
        return reviewRepository.postChildComment(reviewId, bundleId, userId, content);
    }

    // NOTE: 리뷰 좋아요 <-> 좋아요 취소
    @Transactional
    public PostReviewLikeRes postReviewLike(Long reviewId, Long userId) throws BaseException {
        log.info("in reviewService, postReviewLike / reviewId = " + reviewId + ", userId = " + userId);
        if (reviewId == null || userId == null || reviewId < 1 || userId < 1) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        if (userRepository.checkUserExists(userId) == 0) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        if (reviewRepository.checkReviewExists(reviewId) == 0) {
            throw new BaseException(BaseResponseStatus.INVALID_REVIEW_ERROR);
        }
        return reviewRepository.postReviewLike(reviewId, userId);
    }
}
