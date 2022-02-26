package com.dnr2144.csmoa.recipe.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class PostRecipeRes {

    private final Long userId;
    private final Long recipeId;
    private final List<String> recipeImageUrls;

    @Builder
    public PostRecipeRes(Long userId, Long recipeId, List<String> recipeImageUrls) {
        this.userId = userId;
        this.recipeId = recipeId;
        this.recipeImageUrls = recipeImageUrls;
    }
}
