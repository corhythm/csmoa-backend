package com.dnr2144.csmoa.review.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PostReviewLikeRes {

    private final Long userId;
    private final Long reviewId;
    private final Boolean isLike;

    @Builder
    public PostReviewLikeRes(Long userId, Long reviewId, Boolean isLike) {
        this.userId = userId;
        this.reviewId = reviewId;
        this.isLike = isLike;
    }
}
