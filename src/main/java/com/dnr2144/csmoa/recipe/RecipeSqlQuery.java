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

    public static String GET_RECOMMENDED_RECIPES =
            "SELECT recipes.recipe_id,\n" +
                    "       recipes.recipe_title,\n" +
                    "       recipes.recipe_content,\n" +
                    "       recipe_ingredients.ingredients,\n" +
                    "       recipe_likes.like_num,\n" +
                    "       recipe_views.view_num,\n" +
                    "       recipe_images.image_src AS recipe_image_urls,\n" +
                    "       (SELECT temp_recipe_likes.is_like\n" +
                    "        FROM recipe_likes AS temp_recipe_likes\n" +
                    "        WHERE temp_recipe_likes.recipe_id = recipes.recipe_id\n" +
                    "          AND temp_recipe_likes.user_id = :userId) AS is_like,\n" +
                    "       CASE\n" +
                    "           WHEN (TIMESTAMPDIFF(YEAR, recipes.created_at, NOW()) > 1) # 일 년 지나면\n" +
                    "               THEN DATE_FORMAT(recipes.created_at, '%y.%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(HOUR, recipes.created_at, NOW()) > 24) # 하루 지나면\n" +
                    "               THEN DATE_FORMAT(recipes.created_at, '%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(MINUTE, recipes.created_at, NOW()) > 60) # 한 시간 지나면\n" +
                    "               THEN DATE_FORMAT(recipes.created_at, '%H:%i')\n" +
                    "           ELSE CONCAT(TIMESTAMPDIFF(MINUTE, recipes.created_at, NOW()), '분 전') # 한 시간 안 지났으면\n" +
                    "           END                                    AS created_at\n" +
                    "FROM recipes\n" +
                    "         LEFT JOIN (SELECT recipe_id, COUNT(*) AS like_num FROM recipe_likes WHERE is_like = true GROUP BY recipe_id) AS recipe_likes\n" +
                    "                   ON recipe_likes.recipe_id = recipes.recipe_id\n" +
                    "         LEFT JOIN (SELECT recipe_id, COUNT(*) AS view_num FROM recipe_histories GROUP BY recipe_id) AS recipe_views\n" +
                    "                   ON recipe_views.recipe_id = recipes.recipe_id\n" +
                    "         INNER JOIN (SELECT recipe_id, GROUP_CONCAT(image_src SEPARATOR ',') as image_src\n" +
                    "                     FROM recipe_images\n" +
                    "                     GROUP BY recipe_id) AS recipe_images\n" +
                    "                    ON recipe_images.recipe_id = recipes.recipe_id\n" +
                    "         INNER JOIN (SELECT recipe_id, GROUP_CONCAT(ingredient_name SEPARATOR ' + ') AS ingredients\n" +
                    "                     FROM recipe_ingredients\n" +
                    "                     GROUP BY recipe_id) AS recipe_ingredients ON recipe_ingredients.recipe_id = recipes.recipe_id\n" +
                    "ORDER BY (like_num + view_num) DESC\n" +
                    "LIMIT :randomOffset, 10";

    public static String GET_RECIPES =
            "SELECT recipes.recipe_id,\n" +
                    "       recipes.recipe_title,\n" +
                    "       recipes.recipe_content,\n" +
                    "       recipe_ingredients.ingredients,\n" +
                    "       recipe_likes.like_num,\n" +
                    "       recipe_views.view_num,\n" +
                    "       recipe_images.image_src AS recipe_image_urls,\n" +
                    "       (SELECT temp_recipe_likes.is_like\n" +
                    "        FROM recipe_likes AS temp_recipe_likes\n" +
                    "        WHERE temp_recipe_likes.recipe_id = recipes.recipe_id\n" +
                    "          AND temp_recipe_likes.user_id = :userId) AS is_like,\n" +
                    "       CASE\n" +
                    "           WHEN (TIMESTAMPDIFF(YEAR, recipes.created_at, NOW()) > 1) # 일 년 지나면\n" +
                    "               THEN DATE_FORMAT(recipes.created_at, '%y.%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(HOUR, recipes.created_at, NOW()) > 24) # 하루 지나면\n" +
                    "               THEN DATE_FORMAT(recipes.created_at, '%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(MINUTE, recipes.created_at, NOW()) > 60) # 한 시간 지나면\n" +
                    "               THEN DATE_FORMAT(recipes.created_at, '%H:%i')\n" +
                    "           ELSE CONCAT(TIMESTAMPDIFF(MINUTE, recipes.created_at, NOW()), '분 전') # 한 시간 안 지났으면\n" +
                    "           END                                    AS created_at\n" +
                    "FROM recipes\n" +
                    "         LEFT JOIN (SELECT recipe_id, COUNT(*) AS like_num FROM recipe_likes WHERE is_like = true GROUP BY recipe_id) AS recipe_likes\n" +
                    "                   ON recipe_likes.recipe_id = recipes.recipe_id\n" +
                    "         LEFT JOIN (SELECT recipe_id, COUNT(*) AS view_num FROM recipe_histories GROUP BY recipe_id) AS recipe_views\n" +
                    "                   ON recipe_views.recipe_id = recipes.recipe_id\n" +
                    "         INNER JOIN (SELECT recipe_id, GROUP_CONCAT(image_src SEPARATOR ',') as image_src\n" +
                    "                     FROM recipe_images\n" +
                    "                     GROUP BY recipe_id) AS recipe_images\n" +
                    "                    ON recipe_images.recipe_id = recipes.recipe_id\n" +
                    "         INNER JOIN (SELECT recipe_id, GROUP_CONCAT(ingredient_name SEPARATOR ' + ') AS ingredients\n" +
                    "                     FROM recipe_ingredients\n" +
                    "                     GROUP BY recipe_id) AS recipe_ingredients ON recipe_ingredients.recipe_id = recipes.recipe_id\n" +
                    "ORDER BY recipe_id DESC\n" +
                    "LIMIT :pageNum, 10";

    public static String GET_RECIPE_SEARCH_RESULTS =
            "SELECT recipes.recipe_id,\n" +
                    "       recipes.recipe_title,\n" +
                    "       recipes.recipe_content,\n" +
                    "       recipe_ingredients.ingredients,\n" +
                    "       recipe_likes.like_num,\n" +
                    "       recipe_views.view_num,\n" +
                    "       recipe_images.image_src AS recipe_image_urls,\n" +
                    "       (SELECT temp_recipe_likes.is_like\n" +
                    "        FROM recipe_likes AS temp_recipe_likes\n" +
                    "        WHERE temp_recipe_likes.recipe_id = recipes.recipe_id\n" +
                    "          AND temp_recipe_likes.user_id = :userId) AS is_like,\n" +
                    "       CASE\n" +
                    "           WHEN (TIMESTAMPDIFF(YEAR, recipes.created_at, NOW()) > 1) # 일 년 지나면\n" +
                    "               THEN DATE_FORMAT(recipes.created_at, '%y.%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(HOUR, recipes.created_at, NOW()) > 24) # 하루 지나면\n" +
                    "               THEN DATE_FORMAT(recipes.created_at, '%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(MINUTE, recipes.created_at, NOW()) > 60) # 한 시간 지나면\n" +
                    "               THEN DATE_FORMAT(recipes.created_at, '%H:%i')\n" +
                    "           ELSE CONCAT(TIMESTAMPDIFF(MINUTE, recipes.created_at, NOW()), '분 전') # 한 시간 안 지났으면\n" +
                    "           END                                    AS created_at\n" +
                    "FROM recipes\n" +
                    "         LEFT JOIN (SELECT recipe_id, COUNT(*) AS like_num FROM recipe_likes WHERE is_like = true GROUP BY recipe_id) AS recipe_likes\n" +
                    "                   ON recipe_likes.recipe_id = recipes.recipe_id\n" +
                    "         LEFT JOIN (SELECT recipe_id, COUNT(*) AS view_num FROM recipe_histories GROUP BY recipe_id) AS recipe_views\n" +
                    "                   ON recipe_views.recipe_id = recipes.recipe_id\n" +
                    "         INNER JOIN (SELECT recipe_id, GROUP_CONCAT(image_src SEPARATOR ',') as image_src\n" +
                    "                     FROM recipe_images\n" +
                    "                     GROUP BY recipe_id) AS recipe_images\n" +
                    "                    ON recipe_images.recipe_id = recipes.recipe_id\n" +
                    "         INNER JOIN (SELECT recipe_id, GROUP_CONCAT(ingredient_name SEPARATOR ' + ') AS ingredients\n" +
                    "                     FROM recipe_ingredients\n" +
                    "                     GROUP BY recipe_id) AS recipe_ingredients ON recipe_ingredients.recipe_id = recipes.recipe_id\n" +
                    "WHERE recipe_title LIKE :searchWord\n" +
                    "ORDER BY recipe_id DESC\n" +
                    "LIMIT :pageNum, 10";

    public static String GET_DETAILED_RECIPE =
            "SELECT recipes.recipe_id,\n" +
                    "       users.user_id,\n" +
                    "       users.nickname,\n" +
                    "       users.profile_image_url,\n" +
                    "       recipes.recipe_title,\n" +
                    "       recipes.recipe_content,\n" +
                    "       recipe_likes.like_num,\n" +
                    "       recipe_views.view_num,\n" +
                    "       (SELECT temp_recipe_likes.is_like\n" +
                    "        FROM recipe_likes AS temp_recipe_likes\n" +
                    "        WHERE temp_recipe_likes.recipe_id = recipes.recipe_id\n" +
                    "          AND temp_recipe_likes.user_id = :userId) AS is_like,\n" +
                    "       CASE\n" +
                    "           WHEN (TIMESTAMPDIFF(YEAR, recipes.created_at, NOW()) > 1) # 일 년 지나면\n" +
                    "               THEN DATE_FORMAT(recipes.created_at, '%y.%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(HOUR, recipes.created_at, NOW()) > 24) # 하루 지나면\n" +
                    "               THEN DATE_FORMAT(recipes.created_at, '%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(MINUTE, recipes.created_at, NOW()) > 60) # 한 시간 지나면\n" +
                    "               THEN DATE_FORMAT(recipes.created_at, '%H:%i')\n" +
                    "           ELSE CONCAT(TIMESTAMPDIFF(MINUTE, recipes.created_at, NOW()), '분 전') # 한 시간 안 지났으면\n" +
                    "           END                                         AS created_at\n" +
                    "FROM recipes\n" +
                    "         LEFT JOIN (SELECT recipe_id, COUNT(*) AS like_num FROM recipe_likes WHERE is_like = true GROUP BY recipe_id) AS recipe_likes\n" +
                    "                   ON recipe_likes.recipe_id = recipes.recipe_id\n" +
                    "         LEFT JOIN (SELECT recipe_id, COUNT(*) AS view_num FROM recipe_histories GROUP BY recipe_id) AS recipe_views\n" +
                    "                   ON recipe_views.recipe_id = recipes.recipe_id\n" +
                    "         INNER JOIN (SELECT user_id, nickname, profile_image_url FROM users) AS users ON users.user_id = recipes.user_id\n" +
                    "WHERE recipes.recipe_id = :recipeId";

    public static String GET_DETAILED_RECIPE_INGREDIENTS =
            "SELECT ingredient_name, CONCAT(FORMAT(ingredient_price, 0), '원') AS ingredient_price, cs_brand \n" +
                    "FROM recipe_ingredients WHERE recipe_id = ?;";
}
