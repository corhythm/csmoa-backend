package com.dnr2144.csmoa.review;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.login.UserRepository;
import com.dnr2144.csmoa.review.domain.PostReviewReq;
import com.dnr2144.csmoa.review.domain.PostReviewRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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
}
