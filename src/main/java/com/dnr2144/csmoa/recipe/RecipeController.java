package com.dnr2144.csmoa.recipe;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponse;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.recipe.domain.PostRecipeLikeRes;
import com.dnr2144.csmoa.recipe.domain.PostRecipeReq;
import com.dnr2144.csmoa.recipe.domain.PostRecipeRes;
import com.dnr2144.csmoa.recipe.domain.model.DetailedRecipe;
import com.dnr2144.csmoa.recipe.domain.model.Recipe;
import com.dnr2144.csmoa.review.domain.PostReviewLikeRes;
import com.dnr2144.csmoa.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@ResponseBody
public class RecipeController {

    private final JwtService jwtService;
    private final RecipeService recipeService;

    // NOTE: 레시피 등록
    @PostMapping("/recipes")
    public BaseResponse<PostRecipeRes> postRecipe(@RequestHeader("Access-Token") String accessToken,
                                                  PostRecipeReq postRecipeReq) {
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            log.info("postRecipe / userId = " + userId + ", postRecipeReq = " + postRecipeReq);
            PostRecipeRes postRecipeRes = recipeService.postRecipe(userId, postRecipeReq);
            log.info("postRecipeRes = " + postRecipeRes.toString());
            return new BaseResponse<>(postRecipeRes);
        } catch (BaseException ex) {
            log.error("(error) postRecipe: " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

    // NOTE: 추천 레시피 가져오기
    @GetMapping("/recommended-recipes")
    public BaseResponse<List<Recipe>> getRecommendedRecipes(@RequestHeader("Access-Token") String accessToken) {
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            log.info("/recommend-recipes / userId = " + userId);
            List<Recipe> recommendedRecipes = recipeService.getRecommendRecipes(userId);
            log.info("recommendedRecipes.size = " + recommendedRecipes.size() +
                    ", recommendedRecipes = " + recommendedRecipes.toString());
            return new BaseResponse<>(recommendedRecipes);
        } catch (BaseException ex) {
            ex.printStackTrace();
            log.error("((GET) / getRecommendedRecipes): " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

    // NOTE: 레시피 가져오기
    @GetMapping("/recipes")
    public BaseResponse<List<Recipe>> getRecipes(@RequestHeader("Access-Token") String accessToken,
                                                 @RequestParam(required = false, value = "search") String searchWord,
                                                 @RequestParam("page") Integer pageNum) {
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            log.info("/recipes / userId = " + userId + ", searchWord = " + searchWord + ", page = " + pageNum);
            List<Recipe> recipes = recipeService.getRecipes(userId, searchWord, pageNum);
            log.info("recipes.size = " + recipes.size() +
                    ", recipes = " + recipes.toString());
            return new BaseResponse<>(recipes);
        } catch (BaseException ex) {
            ex.printStackTrace();
            log.error("((GET) / getRecipes): " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

    // NOTE: 세부 레시피 가져오기
    @GetMapping("/recipes/{recipeId}")
    public BaseResponse<DetailedRecipe> getDetailedRecipe(@RequestHeader("Access-Token") String accessToken,
                                                          @PathVariable("recipeId") Long recipeId) {
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            log.info("/detailedRecipe / userId = " + userId);
            DetailedRecipe detailedRecipe = recipeService.getDetailedRecipe(userId, recipeId);
            log.info("detailedRecipe = " + detailedRecipe.toString());
            return new BaseResponse<>(detailedRecipe);
        } catch (BaseException ex) {
            ex.printStackTrace();
            log.error("((GET) / getDetailedRecipe): " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }

    // NOTE: 레시피 좋아요 / 좋아요 취소
    @PostMapping("/recipes/{recipeId}/like")
    public BaseResponse<PostRecipeLikeRes> postRecipeLike(@PathVariable("recipeId") Long recipeId,
                                                          @RequestHeader("Access-Token") String accessToken) {
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            log.info("recipeId = " + recipeId + ", userId = " + userId);
            PostRecipeLikeRes postRecipeLikeRes = recipeService.postRecipeLike(recipeId, userId);
            log.info("postRecipeLikeRes = " + postRecipeLikeRes.toString());
            return new BaseResponse<>(postRecipeLikeRes);
        } catch (BaseException ex) {
            ex.printStackTrace();
            log.error("postReviewLike: " + ex.getStatus().toString());
            return new BaseResponse<>(ex.getStatus());
        }
    }
}
