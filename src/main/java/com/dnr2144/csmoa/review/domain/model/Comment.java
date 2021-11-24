package com.dnr2144.csmoa.review.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Comment {
    private final Long reviewCommentId;
    private final Long userId;
    private final String nickname;
    private final String userProfileImageUrl;
    private final Long bundleId;
    private final String commentContent;
    private final Integer nestedCommentNum;
    private final String createdAt;

    @Builder
    public Comment(Long reviewCommentId, Long userId, String nickname, String userProfileImageUrl,
                   Long bundleId, String commentContent, Integer nestedCommentNum, String createdAt) {
        this.reviewCommentId = reviewCommentId;
        this.userId = userId;
        this.nickname = nickname;
        this.userProfileImageUrl = userProfileImageUrl;
        this.bundleId = bundleId;
        this.commentContent = commentContent;
        this.nestedCommentNum = nestedCommentNum;
        this.createdAt = createdAt;
    }
}
