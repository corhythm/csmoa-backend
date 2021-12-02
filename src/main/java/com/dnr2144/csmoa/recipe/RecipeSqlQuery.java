package com.dnr2144.csmoa.recipe;

public class RecipeSqlQuery {

    // // select recipe_id, user_id, recipe_title, recipe_content, created_at, updated_at, status from recipes;
    public static final String INSERT_RECIPE =
            "INSERT INTO recipes (user_id, recipe_title, recipe_content) VALUE (?, ?, ?)";

    public static String INSERT_RECIPE_IMAGES =
            "INSERT INTO recipe_images (recipe_id, image_src) value (:recipeId, :imageSrc)"; // 테스트

    // INSERT INTO recipe_ingredients (recipe_ingredient_id, recipe_id, ingredient_name, ingredient_price, csBrand, created_at, updated_at, status) VALUE ()
    public static String INSERT_RECIPE_INGREDIENT =
            "INSERT INTO recipe_ingredients (recipe_id, ingredient_name, ingredient_price, cs_brand) " +
                    "VALUE (:recipeId, :ingredientName, :ingredientPrice, :csBrand)";
}
