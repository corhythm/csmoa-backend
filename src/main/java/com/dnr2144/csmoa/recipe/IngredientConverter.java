package com.dnr2144.csmoa.recipe;

import com.dnr2144.csmoa.recipe.domain.model.Ingredient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IngredientConverter implements Converter<String, List<Ingredient>> {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public List<Ingredient> convert(String source) {
//        return null;
        return objectMapper.readValue(source, new TypeReference<>() {});
    }

    @Override
    public <U> Converter<String, U> andThen(Converter<? super List<Ingredient>, ? extends U> after) {
        return Converter.super.andThen(after);
    }
}
