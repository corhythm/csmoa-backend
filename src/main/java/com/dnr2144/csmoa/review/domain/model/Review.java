package com.dnr2144.csmoa.review.domain.model;

import lombok.*;

import java.util.List;

@ToString
@Getter
public class Review {
    private final Long reviewId;
    private final Long userId;
    private final String reviewName;
    private final String price;
    private final Float starScore;
    private final List<String> reviewImageUrls;
    private final String csBrand;
    private final String content;
    private final Integer likeNum;
    private final Integer commentNum;
    private final Integer viewNum;
    private final String createdAt;
    private final Boolean isLike;

    @Builder
    public Review(Long reviewId, Long userId, String reviewName, String price,
                  Float starScore, List<String> reviewImageUrls, String csBrand,
                  String content, Integer likeNum, Integer commentNum, Integer viewNum,
                  String createdAt, Boolean isLike) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.reviewName = reviewName;
        this.price = price;
        this.starScore = starScore;
        this.reviewImageUrls = reviewImageUrls;
        this.csBrand = csBrand;
        this.content = content;
        this.likeNum = likeNum;
        this.commentNum = commentNum;
        this.viewNum = viewNum;
        this.createdAt = createdAt;
        this.isLike = isLike;
    }
}
