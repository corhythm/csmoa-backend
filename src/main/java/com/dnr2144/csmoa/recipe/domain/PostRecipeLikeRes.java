package com.dnr2144.csmoa.recipe.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class PostRecipeLikeRes {

    @JsonProperty("recipeId") private final Long recipeId;
    @JsonProperty("userId") private final Long userId;
    @JsonProperty("isLike") private final Boolean isLike;

    @Builder
    public PostRecipeLikeRes(Long recipeId, Long userId, Boolean isLike) {
        this.recipeId = recipeId;
        this.userId = userId;
        this.isLike = isLike;
    }
}
