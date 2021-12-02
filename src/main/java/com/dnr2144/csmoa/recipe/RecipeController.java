package com.dnr2144.csmoa.recipe;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponse;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.recipe.domain.PostRecipeReq;
import com.dnr2144.csmoa.recipe.domain.PostRecipeRes;
import com.dnr2144.csmoa.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RecipeController {

    private final JwtService jwtService;
    private final RecipeService recipeService;

    // NOTE: 레시피 등록
    @PostMapping("/recipes")
    @ResponseBody
    public BaseResponse<PostRecipeRes> postRecipe(@RequestHeader("Access-Token") String accessToken, PostRecipeReq postRecipeReq) {
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


}
