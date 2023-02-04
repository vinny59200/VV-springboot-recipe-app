package com.vv.recipe.vv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.vv.recipe.vv.model.VVRecipe;

import java.util.List;


public interface RecipeRepository extends JpaRepository<VVRecipe, Integer> {
    @Query(value="SELECT * FROM VVRECIPE r WHERE LOWER(r.NAME) LIKE LOWER(concat('%', :name,'%')) ORDER BY r.date DESC",nativeQuery = true)
    List<VVRecipe> findByName(@Param("name")String name);

    @Query(value="SELECT * FROM VVRECIPE r WHERE LOWER(r.CATEGORY) LIKE LOWER(:category) ORDER BY r.date DESC",nativeQuery = true)
    List<VVRecipe> findByCategory(@Param("category")String category);
}
