package com.dnr2144.csmoa.review.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
public class DetailedReview {
    private Long reviewId;
    private Long userId;
    private String userProfileImageUrl;
    private String nickname;
    private String itemName;
    private String itemPrice;
    private Float itemStarScore;
    private List<String> itemImageUrls;
    private String csBrand;
    private String content;
    private Integer likeNum;
    private Integer commentNum;
    private Integer viewNum;
    private String createdAt;
    private Boolean isLike;

    @Builder
    public DetailedReview(Long reviewId, Long userId, String userProfileImageUrl,
                          String nickname, String itemName, String itemPrice, Float itemStarScore,
                          List<String> itemImageUrls, String csBrand, String content, Integer likeNum,
                          Integer commentNum, Integer viewNum, String createdAt, Boolean isLike) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.userProfileImageUrl = userProfileImageUrl;
        this.nickname = nickname;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemStarScore = itemStarScore;
        this.itemImageUrls = itemImageUrls;
        this.csBrand = csBrand;
        this.content = content;
        this.likeNum = likeNum;
        this.commentNum = commentNum;
        this.viewNum = viewNum;
        this.createdAt = createdAt;
        this.isLike = isLike;
    }
}
