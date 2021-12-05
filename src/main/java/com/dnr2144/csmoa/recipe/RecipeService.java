package com.dnr2144.csmoa.recipe;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.login.UserRepository;
import com.dnr2144.csmoa.recipe.domain.PostRecipeLikeRes;
import com.dnr2144.csmoa.recipe.domain.PostRecipeReq;
import com.dnr2144.csmoa.recipe.domain.PostRecipeRes;
import com.dnr2144.csmoa.recipe.domain.model.DetailedRecipe;
import com.dnr2144.csmoa.recipe.domain.model.Recipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecipeService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    @Transactional
    PostRecipeRes postRecipe(Long userId, PostRecipeReq postRecipeReq) throws BaseException {
        if (userId == null || postRecipeReq == null || postRecipeReq.getRecipeImages() == null ||
                postRecipeReq.getContent() == null || postRecipeReq.getIngredients() == null) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        if (userRepository.checkUserExists(userId) == 0) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        return recipeRepository.postRecipe(userId, postRecipeReq);
    }

    // NOTE: 추천 레시피 가져오기
    public List<Recipe> getRecommendRecipes(Long userId) throws BaseException {
        if (userId == null || userId < 0) { // 입력값 null 체크
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        // 존재하지 않는 유저일 때
        if (userRepository.checkUserExists(userId) == 0) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        return recipeRepository.getRecommendedRecipes(userId);
    }

    // NOTE: 일반 레시피 가져오기
    public List<Recipe> getRecipes(Long userId, String searchWord, Integer pageNum) throws BaseException {
        if (userId == null || pageNum == null || userId < 0 || pageNum < 0) { // 입력값 null 체크
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        // 존재하지 않는 유저일 때
        if (userRepository.checkUserExists(userId) == 0) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        return recipeRepository.getRecipes(userId, searchWord, pageNum);
    }

    // NOTE: 레시피 세부 정보 가져오기
    public DetailedRecipe getDetailedRecipe(Long userId, Long recipeId) throws BaseException {
        if (userId == null || recipeId == null || userId < 0 || recipeId < 0) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        // 존재하지 않는 유저일 때
        if (userRepository.checkUserExists(userId) == 0) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        // 레시피가 존재하지 않을 때
        if (recipeRepository.checkRecipeExists(recipeId) == 0) {
            throw new BaseException(BaseResponseStatus.INVALID_RECIPE_ERROR);
        }
        return recipeRepository.getDetailedRecipe(userId, recipeId);
    }

    // NOTE: 레시피 좋아요 <-> 좋아요 취소
    @Transactional // 동작을 안 해...
    public PostRecipeLikeRes postRecipeLike(Long recipeId, Long userId) throws BaseException {
        if (recipeId == null || userId == null || recipeId < 1 || userId < 1) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        if (userRepository.checkUserExists(userId) == 0) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        if (recipeRepository.checkRecipeExists(recipeId) == 0) {
            throw new BaseException(BaseResponseStatus.INVALID_RECIPE_ERROR);
        }
        return recipeRepository.postRecipeLike(recipeId, userId);
    }
}
