package com.dnr2144.csmoa.recipe.domain;

import com.dnr2144.csmoa.recipe.domain.model.Ingredient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
public class PostRecipeReq {

    private final List<MultipartFile> recipeImages;
    private final String name;
    private final String content;
    private final List<Ingredient> ingredients;
}
