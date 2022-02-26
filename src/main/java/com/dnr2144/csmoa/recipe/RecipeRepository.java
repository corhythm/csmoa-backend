package com.dnr2144.csmoa.recipe;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.firebase.FirebaseStorageManager;
import com.dnr2144.csmoa.recipe.domain.PostRecipeLikeRes;
import com.dnr2144.csmoa.recipe.domain.PostRecipeReq;
import com.dnr2144.csmoa.recipe.domain.PostRecipeRes;
import com.dnr2144.csmoa.recipe.domain.model.DetailedRecipe;
import com.dnr2144.csmoa.recipe.domain.model.Ingredient;
import com.dnr2144.csmoa.recipe.domain.model.Recipe;
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
import java.util.*;

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
            for (Ingredient ingredient : ingredients) {
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

    // NOTE: 추천 레시피 리스트 받아오기)
    public List<Recipe> getRecommendedRecipes(long userId) throws BaseException {
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("randomOffset", new Random().nextInt(40));

            return namedParameterJdbcTemplate.query(RecipeSqlQuery.GET_RECOMMENDED_RECIPES, params,
                    (rs, row) -> Recipe.builder()
                            .recipeId(rs.getLong("recipe_id"))
                            .recipeName(rs.getString("recipe_title"))
                            .recipeContent(rs.getString("recipe_content"))
                            .ingredients(rs.getString("ingredients"))
                            .likeNum(rs.getInt("like_num"))
                            .viewNum(rs.getInt("view_num"))
                            .isLike(rs.getBoolean("is_like"))
                            .recipeImageUrls(Arrays.asList(
                                    rs.getString("recipe_image_urls").split(",", -1))
                            )
                            .createdAt(rs.getString("created_at"))
                            .build());

        } catch (Exception exception) {
            exception.printStackTrace();
            log.info("(in RecipeRepository, getRecommendedRecipes) " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 일반 레시피 리스트 받아오기)
    public List<Recipe> getRecipes(long userId, String searchWord, int pageNum) throws BaseException {
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("pageNum", (pageNum - 1) * 10);

            String query = "";
            if (searchWord == null) { // 일반 리뷰
                log.info("(in ReviewRepository) 일반 레시피");
                query = RecipeSqlQuery.GET_RECIPES;

            } else { // 검색된 레시피
//                params.put("searchWord", "%" + searchWord + "%");
                params.put("searchWord", "*" + searchWord + "*");
                log.info("(in ReviewRepository) 레시피 검색");
                query = RecipeSqlQuery.GET_RECIPE_SEARCH_RESULTS;
            }

            return namedParameterJdbcTemplate.query(query, params,
                    (rs, row) -> Recipe.builder()
                            .recipeId(rs.getLong("recipe_id"))
                            .recipeName(rs.getString("recipe_title"))
                            .recipeContent(rs.getString("recipe_content"))
                            .ingredients(rs.getString("ingredients"))
                            .likeNum(rs.getInt("like_num"))
                            .viewNum(rs.getInt("view_num"))
                            .isLike(rs.getBoolean("is_like"))
                            .recipeImageUrls(Arrays.asList(
                                    rs.getString("recipe_image_urls").split(",", -1))
                            )
                            .createdAt(rs.getString("created_at"))
                            .build());

        } catch (Exception exception) {
            exception.printStackTrace();
            log.info("(in RecipeRepository, getRecommendedRecipes) " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 레시피 세부 정보 가져오기
    public DetailedRecipe getDetailedRecipe(long userId, long recipeId) throws BaseException {
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("recipeId", recipeId);

            // 재료 리스트 가져오기
            List<Ingredient> ingredients = jdbcTemplate.query(RecipeSqlQuery.GET_DETAILED_RECIPE_INGREDIENTS,
                    (rs, row) -> Ingredient.builder()
                            .name(rs.getString("ingredient_name"))
                            .price(rs.getString("ingredient_price"))
                            .csBrand(rs.getString("cs_brand"))
                            .build(), recipeId);

            // 레시피 이미지 리스트 가져오기
            String getRecipeImageUrlsQuery = "SELECT image_src FROM recipe_images WHERE recipe_id = ?";
            List<String> recipeImageUrls = jdbcTemplate.queryForList(getRecipeImageUrlsQuery, String.class, recipeId);

            DetailedRecipe detailedRecipe = namedParameterJdbcTemplate.queryForObject(RecipeSqlQuery.GET_DETAILED_RECIPE, params,
                    (rs, row) -> DetailedRecipe
                            .builder()
                            .recipeId(rs.getLong("recipe_id"))
                            .userId(rs.getLong("user_id"))
                            .userNickname(rs.getString("nickname"))
                            .userProfileImageUrl(rs.getString("profile_image_url"))
                            .recipeName(rs.getString("recipe_title"))
                            .ingredients(ingredients)
                            .recipeImageUrls(recipeImageUrls)
                            .recipeContent(rs.getString("recipe_content"))
                            .viewNum(rs.getInt("view_num"))
                            .likeNum(rs.getInt("like_num"))
                            .isLike(rs.getBoolean("is_like"))
                            .createdAt(rs.getString("created_at"))
                            .build());

            // 히스토리 기록 추가
            // 히스토리 추가
            String postRecipeHistoryQuery = "INSERT INTO recipe_histories (recipe_id, user_id) VALUE (?, ?)";
            jdbcTemplate.update(postRecipeHistoryQuery, recipeId, userId);
            detailedRecipe.setViewNum(detailedRecipe.getViewNum() + 1); // 모든 게 성공하면 조회수 ++

            return detailedRecipe;

        } catch (Exception exception) {
            log.error("getDetailedRecipe / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 레시피 좋아요 OR 좋아요 취소
    public PostRecipeLikeRes postRecipeLike(long recipeId, long userId) throws BaseException {
        try {
            Boolean isLike = getRecipeLike(recipeId, userId);
            if (isLike == null) {
                String insertRecipeLikeQuery = "INSERT INTO recipe_likes (recipe_id, user_id, is_like) VALUE (?, ?, ?);";
                jdbcTemplate.update(insertRecipeLikeQuery, recipeId, userId, true);
                isLike = true;
            } else {
                // 좋아요 <-> 싫어요
                String updateRecipeLikeQuery = "";
                if (Boolean.TRUE.equals(isLike)) {  // 좋아요 -> 싫어요
                    updateRecipeLikeQuery = "UPDATE recipe_likes SET is_like = false, updated_at = CURRENT_TIMESTAMP WHERE recipe_id = ? AND user_id = ?;";
                } else {  // 싫어요 -> 좋아요
                    updateRecipeLikeQuery = "UPDATE recipe_likes SET is_like = true, updated_at = CURRENT_TIMESTAMP WHERE recipe_id = ? AND user_id = ?;";
                }
                isLike = !isLike;
                jdbcTemplate.update(updateRecipeLikeQuery, recipeId, userId);
            }

            return PostRecipeLikeRes.builder()
                    .userId(userId)
                    .recipeId(recipeId)
                    .isLike(isLike)
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("In RecipeRepository, postRecipeLike = " + ex.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 레시피 좋아요 한 이력이 있는지 알아보기
    public Boolean getRecipeLike(long recipeId, long userId) throws BaseException {
        try {
            String checkExistsRecipeLikeQuery = "SELECT EXISTS(SELECT is_like FROM recipe_likes WHERE recipe_id = ? and user_id = ?)";
            int isRecipeLikeExists = jdbcTemplate.queryForObject(checkExistsRecipeLikeQuery, Integer.class, recipeId, userId);
            if (isRecipeLikeExists == 0) return null;

            // 이전 기록이 있으면
            String getRecipeLikeQuery = "SELECT is_like FROM recipe_likes WHERE recipe_id = ? and user_id = ?";
            return jdbcTemplate.queryForObject(getRecipeLikeQuery, Boolean.class, recipeId, userId);
        } catch (Exception exception) {
            log.error("getRecipeLike =  / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 레시피가 존재하는지 체크
    public Integer checkRecipeExists(long recipeId) throws BaseException {
        try {
            String checkRecipeExistsQuery = "SELECT EXISTS(SELECT * FROM recipes WHERE recipe_id = ?)";
            return jdbcTemplate.queryForObject(checkRecipeExistsQuery, Integer.class, recipeId);
        } catch (Exception exception) {
            log.error("checkRecipeExists / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
