package com.dnr2144.csmoa.recipe.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class DetailedRecipe {
    private final Long recipeId;
    private final Long userId;
    private final String userNickname;
    private final String userProfileImageUrl;
    private final String recipeName;
    @Setter private List<String> recipeImageUrls;
    @Setter private List<Ingredient> ingredients;
    private final String recipeContent;
    @Setter private Integer viewNum;
    private final Integer likeNum;
    private final Boolean isLike;
    private final String createdAt;

    @Builder
    public DetailedRecipe(Long recipeId, Long userId, String userNickname, String userProfileImageUrl,
                          String recipeName, List<String> recipeImageUrls, List<Ingredient> ingredients,
                          String recipeContent, Integer viewNum, Integer likeNum, Boolean isLike, String createdAt) {
        this.recipeId = recipeId;
        this.userId = userId;
        this.userNickname = userNickname;
        this.userProfileImageUrl = userProfileImageUrl;
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.recipeImageUrls = recipeImageUrls;
        this.recipeContent = recipeContent;
        this.viewNum = viewNum;
        this.likeNum = likeNum;
        this.isLike = isLike;
        this.createdAt = createdAt;
    }
}
