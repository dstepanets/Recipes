package recipes.rest;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import recipes.entities.Recipe;
import recipes.repo.RecipesRepository;

import javax.validation.Valid;
import java.time.LocalDateTime;


@RestController
public class RecipesController {
    
    private final RecipesRepository recipesRepository;

    @Autowired
    public RecipesController(RecipesRepository recipesRepository) {
        this.recipesRepository = recipesRepository;
    }

    @GetMapping("/api/recipe/{id}")
    public Recipe getRecipe(@PathVariable Long id) {
        return recipesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = "/api/recipe/new")
    public RecipeId addRecipe(@RequestBody @Valid Recipe recipe) {
        recipe.setDate(LocalDateTime.now());
        return new RecipeId(recipesRepository.save(recipe).getId());
    }
    
    @PutMapping("/api/recipe/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRecipe(@PathVariable Long id, @RequestBody @Valid Recipe recipe) {
        if (recipesRepository.existsById(id)) {
            recipe.setId(id);
            recipe.setDate(LocalDateTime.now());
            recipesRepository.save(recipe);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/api/recipe/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable Long id) {
        if (recipesRepository.existsById(id)) {
            recipesRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
    
    @Data
    private static class RecipeId {
        private final Long id;
    }
}
