package com.vv.recipe.vv.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.vv.recipe.vv.exception.BadRequestException;
import com.vv.recipe.vv.exception.ForbiddenException;
import com.vv.recipe.vv.exception.NotFoundException;
import com.vv.recipe.vv.model.VVRecipe;
import com.vv.recipe.vv.model.VVRecipeLite;
import com.vv.recipe.vv.model.VVRegistration;
import com.vv.recipe.vv.service.RecipeService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@Slf4j
public class RecipeController {
    @Autowired
    private RecipeService recipeService;

    @PostMapping(value = "/api/recipe/new", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Integer> createRecipe(@RequestBody VVRecipe recipe) {
        checkRecipe(recipe);
        log.info("<## VV ##> POST->"+recipe);
        VVRecipe result = recipeService.save(recipe);
        Map<String, Integer> response = Map.of("id", result.getId());

        return response;
    }

    private void checkRecipe(VVRecipe recipe) {
        if (recipe.getName() == null || recipe.getName().isBlank()
                || recipe.getDescription() == null || recipe.getDescription().isBlank()
                || recipe.getIngredients() == null || recipe.getIngredients().isEmpty()
                || recipe.getDirections() == null || recipe.getDirections().isEmpty()
                || recipe.getCategory() == null || recipe.getCategory().isBlank()) {
            throw new BadRequestException();
        }
    }

    @GetMapping("/api/recipe/{id}")
    public VVRecipeLite getRecipe(@PathVariable("id") int vvRecipeId) {
        VVRecipe recipe = recipeService.find(vvRecipeId);
        checkNotFound(recipe);
        log.info("VV9: get recipe->"+vvRecipeId);
        return VVRecipeLite.toLite(recipe);
    }

    private void checkNotFound(VVRecipe recipe) {
        if (recipe == null) {
            log.info("VV9: recipe not found");
            throw new NotFoundException();
        }
    }

    @DeleteMapping("/api/recipe/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable("id") int vvRecipeId) {
        VVRecipe recipe = recipeService.find(vvRecipeId);
        checkNotFound(recipe);
        checkIfUserIsOwner(recipe);
        log.info("VV9: delete recipe->"+vvRecipeId);
        recipeService.delete(vvRecipeId);
    }

    private void checkIfUserIsOwner(VVRecipe recipe) {
        log.info("VV16 owner [ctrlr.checkIfUserIsOwner]->"+recipe.getOwner());
        log.info("VV16 recipe [ctrlr.checkIfUserIsOwner]->"+recipe);
        String currentUser=SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentUser.toLowerCase(Locale.ROOT).equals(recipe.getOwner().toLowerCase(Locale.ROOT))) {
            throw new ForbiddenException();
        }
    }

    @PutMapping("/api/recipe/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateRecipe(@PathVariable("id") int vvRecipeId, @RequestBody VVRecipe recipe) {
//        if (recipe.getId() != vvRecipeId) {
//            throw new BadRequestException();
//        }
        VVRecipe existing = recipeService.find(vvRecipeId);
        checkNotFound(existing);
        checkRecipe(recipe);
        checkIfUserIsOwner(existing);
        log.info("<## VV ##> PUT");
        recipeService.save(recipe.toBuilder().id(vvRecipeId).build());
    }

    @GetMapping("/api/recipe/search")
    public List<VVRecipeLite> search(@RequestParam(value = "name", required = false) String name,
                                     @RequestParam(value = "category", required = false) String category) {
        List<VVRecipe> result = recipeService.search(name, category);
        log.info("VV9: search recipes" + result.size());
        return VVRecipeLite.toLiteList(result);
    }

    @PostMapping(value = {"/api/register","/api/register/new"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public void createRegsitration(@RequestBody VVRegistration registration) {
        log.info("<## VV ##> POST registration");
        checkEmail(registration);
        checkPassword(registration);
        if (recipeService.alreadyExists(registration.getEmail())) {
            throw new BadRequestException();
        }
        VVRegistration result = recipeService.saveRegistration(registration);
        log.info("VV9 <## VV ##> POST registration result: " + result);
    }

    private void checkPassword(VVRegistration registration) {
        if (registration.getPassword() == null || registration.getPassword().isBlank()) {
            log.info("VV9 <## VV ##> registration password is null");
            throw new BadRequestException();
        }
        if (registration.getPassword().length() < 8) {
            log.info("VV9 <## VV ##> registration password is too short");
            throw new BadRequestException();
        }
    }

    private void checkEmail(VVRegistration registration) {
        if (registration.getEmail() == null || registration.getEmail().isBlank()) {
            log.info("VV9 <## VV ##> registration email is null");
            throw new BadRequestException();
        }
        EmailValidator emailValidator = EmailValidator.getInstance();
        if (!emailValidator.isValid(registration.getEmail())) {
            log.info("VV9 <## VV ##> registration email is invalid");
            throw new BadRequestException();
        }
    }
}
