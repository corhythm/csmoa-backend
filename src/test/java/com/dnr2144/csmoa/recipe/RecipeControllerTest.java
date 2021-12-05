package com.dnr2144.csmoa.recipe;

import com.dnr2144.csmoa.recipe.domain.PostRecipeLikeRes;
import com.dnr2144.csmoa.recipe.domain.PostRecipeRes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RecipeControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void Get에서는_setter가_없어도된다() throws Exception {
        String content = objectMapper.writeValueAsString(PostRecipeLikeRes.builder().recipeId(1L).userId(4L).isLike(false).build());

        mvc.perform(post("/recipes/1/like")
                        .header("Access-Token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjQsImlhdCI6MTYzODU5MTU2MiwiZXhwIjoxNjM4Njc3OTYyfQ.01NM_8f7p7wyO6fKRcJx6sQC7IXSvOYwxEoU56z4Ilo")
//                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(content));
    }

    @Test
    void StringSplitTest() {

    }
}