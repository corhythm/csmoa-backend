package com.dnr2144.csmoa.review.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PostReviewRes {

    private final Long userId;
    private final Long reviewId;
    private final List<String> reviewImageUrls;

    @Builder
    public PostReviewRes(Long userId, Long reviewId, List<String> reviewImageUrls) {
        this.userId = userId;
        this.reviewId = reviewId;
        this.reviewImageUrls = reviewImageUrls;
    }
}
