package com.dnr2144.csmoa.recipe;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.firebase.FirebaseStorageManager;
import com.dnr2144.csmoa.recipe.domain.PostRecipeReq;
import com.dnr2144.csmoa.recipe.domain.PostRecipeRes;
import com.dnr2144.csmoa.recipe.domain.model.Ingredient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@Slf4j
@RequiredArgsConstructor
public class RecipeRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final FirebaseStorageManager firebaseStorageManager;
    private final DataSourceTransactionManager transactionManager;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    // NOTE: 새로운 꿀조합 레시피 등록
    public PostRecipeRes postRecipe(long userId, PostRecipeReq postRecipeReq) throws BaseException {
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(def);

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            // "INSERT INTO recipes (user_id, recipe_title, recipe_content) VALUE (?, ?, ?)";
            jdbcTemplate.update((PreparedStatementCreator) con -> {
                PreparedStatement preparedStatement = con.prepareStatement(
                        RecipeSqlQuery.INSERT_RECIPE, new String[]{"recipe_id"}
                );
                preparedStatement.setLong(1, userId);
                preparedStatement.setString(2, postRecipeReq.getName());
                preparedStatement.setString(3, postRecipeReq.getContent());

                return preparedStatement;
            }, keyHolder);

            // Get PK
            long recipeId = Objects.requireNonNull(keyHolder.getKey()).longValue();
            log.info("generated recipeId = " + recipeId);

            // 파이어베이스에 리뷰 이미지 넣기
            List<String> recipeImagesAbsoluteUrls =
                    firebaseStorageManager.saveRecipeImages(userId, postRecipeReq.getRecipeImages());

            // 레시피 이미지 삽입
            Map<String, Object> recipeImageParams = new HashMap();
            recipeImageParams.put("recipeId", recipeId);
            for (String recipeImageUrl : recipeImagesAbsoluteUrls) {
                recipeImageParams.put("imageSrc", recipeImageUrl);
                namedParameterJdbcTemplate.update(RecipeSqlQuery.INSERT_RECIPE_IMAGES, recipeImageParams);
            }

            // 레시피 재료 삽입
            Map<String, Object> recipeIngredientParams = new HashMap<>();
            recipeIngredientParams.put("recipeId", recipeId);
            List<Ingredient> ingredients = postRecipeReq.getIngredients();
            for(Ingredient ingredient : ingredients) {
                //:ingredientName, :ingredientPrice, :csBrand
                recipeIngredientParams.put("ingredientName", ingredient.getName());
                recipeIngredientParams.put("ingredientPrice", ingredient.getPrice());
                recipeIngredientParams.put("csBrand", ingredient.getCsBrand());
                namedParameterJdbcTemplate.update(RecipeSqlQuery.INSERT_RECIPE_INGREDIENT, recipeIngredientParams);
            }

            transactionManager.commit(transactionStatus);

            return PostRecipeRes.builder()
                    .recipeId(recipeId)
                    .userId(userId)
                    .recipeImageUrls(recipeImagesAbsoluteUrls)
                    .build();

        } catch (Exception exception) {
            exception.printStackTrace();
            log.info("(in ReviewRepository, postReview) " + exception.getMessage());
            transactionManager.rollback(transactionStatus);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
