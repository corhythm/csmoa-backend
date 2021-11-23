package com.dnr2144.csmoa.review.domain.model;

import lombok.*;

@ToString
@Getter
public class Review {
    private final Long reviewId;
    private final Long userId;
    private final String itemName;
    private final String itemPrice;
    private final Float itemStarScore;
    private final String itemImageUrl;
    private final String csBrand;
    private final String content;
    private final Integer likeNum;
    private final Integer commentNum;
    private final Integer viewNum;
    private final String createdAt;
    private final Boolean isLike;

    @Builder
    public Review(Long reviewId, Long userId, String itemName, String itemPrice, Float itemStarScore,
                  String itemImageUrl, String csBrand, String content, Integer likeNum,
                  Integer commentNum, Integer viewNum, String createdAt, Boolean isLike) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemStarScore = itemStarScore;
        this.itemImageUrl = itemImageUrl;
        this.csBrand = csBrand;
        this.content = content;
        this.likeNum = likeNum;
        this.commentNum = commentNum;
        this.viewNum = viewNum;
        this.createdAt = createdAt;
        this.isLike = isLike;
    }
}
