package com.dnr2144.csmoa.recipe.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class Ingredient {
    private String name;
    private Integer price;
    private String csBrand;

    @Builder
    public Ingredient(String name, Integer price, String csBrand) {
        this.name = name;
        this.price = price;
        this.csBrand = csBrand;
    }
}
