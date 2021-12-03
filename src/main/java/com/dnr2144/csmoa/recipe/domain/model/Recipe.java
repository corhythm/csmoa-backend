package com.dnr2144.csmoa.recipe.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class Recipe {
    private final Long recipeId;
    private final String recipeName;
    private final String recipeMainImageUrl;
    private final String recipeContent;
    private final String ingredients;
    private final Integer viewNum;
    private final Integer likeNum;
    private final Boolean isLike;

    @Builder
    public Recipe(Long recipeId, String recipeName, String recipeMainImageUrl, String recipeContent,
                  String ingredients, Integer viewNum, Integer likeNum, Boolean isLike) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.recipeMainImageUrl = recipeMainImageUrl;
        this.recipeContent = recipeContent;
        this.ingredients = ingredients;
        this.viewNum = viewNum;
        this.likeNum = likeNum;
        this.isLike = isLike;
    }
}
