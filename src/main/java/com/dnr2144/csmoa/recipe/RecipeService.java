package com.dnr2144.csmoa.recipe;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.login.UserRepository;
import com.dnr2144.csmoa.recipe.domain.PostRecipeReq;
import com.dnr2144.csmoa.recipe.domain.PostRecipeRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
