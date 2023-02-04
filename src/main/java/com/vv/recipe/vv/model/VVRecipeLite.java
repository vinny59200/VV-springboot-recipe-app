package com.vv.recipe.vv.model;

import lombok.*;
import org.apache.tomcat.jni.Local;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@ToString
public class VVRecipeLite {

    private String name;

    private String description;

    private String[] ingredients;

    private String[] directions;

    private String category;

    private LocalDateTime date;

    public static List<VVRecipeLite> toLiteList(List<VVRecipe> result) {
        return result.stream().map(VVRecipeLite::toLite).collect(Collectors.toList());
    }

    public static VVRecipeLite toLite(VVRecipe recipe) {
        return VVRecipeLite.builder()
                .name(recipe.getName())
                .description(recipe.getDescription())
                .ingredients(getArraySafely(recipe.getIngredients()))
                .directions(getArraySafely(recipe.getDirections()))
                .category(recipe.getCategory())
                .date(recipe.getDate())
                .build();
    }

    private static String[] getArraySafely(List<String> ingredients) {
        if (ingredients == null) {
            return new String[0];
        }
        return ingredients.toArray(new String[ingredients.size()]);
    }
}
