package recipes.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import recipes.entities.Recipe;
import recipes.entities.User;
import recipes.repo.RecipesRepository;
import recipes.repo.UsersRepository;
import recipes.rest.model.RecipeId;
import recipes.rest.model.UserCredentials;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@RestController
public class RecipesController {
    
    private final RecipesRepository recipesRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RecipesController(RecipesRepository recipesRepository, UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.recipesRepository = recipesRepository;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/api/register")
    public void registerUser(@RequestBody @Valid UserCredentials credentials) {
        if (usersRepository.findByEmail(credentials.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        String encryptedPassword = passwordEncoder.encode(credentials.getPassword());
        usersRepository.save(new User(credentials.getEmail(), encryptedPassword));
    }

    @GetMapping("/api/recipe/{id}")
    public Recipe getRecipe(@PathVariable Long id) {
        return recipesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/api/recipe/search")
    public List<Recipe> searchForRecipe(@RequestParam Map<String,String> params) {
        if (params.size() != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        
        String p;
        if ((p = params.get("category")) != null) {
            return recipesRepository.findAllByCategoryIgnoreCaseOrderByDateDesc(p); 
        } else if ((p = params.get("name")) != null) {
            return recipesRepository.findAllByNameContainingIgnoreCaseOrderByDateDesc(p);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/api/recipe/new")
    public RecipeId addRecipe(@RequestBody @Valid Recipe recipe) {
        recipe.setUser(getAuthenticatedUser());
        return new RecipeId(recipesRepository.save(recipe).getId());
    }

    @PutMapping("/api/recipe/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRecipe(@PathVariable Long id, @RequestBody @Valid Recipe recipe) {
        checkRecipeExistsAndUserIsAuthor(id);
        recipe.setId(id);
        recipe.setUser(getAuthenticatedUser());
        recipesRepository.save(recipe);
    }

    @DeleteMapping("/api/recipe/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable Long id) {
        checkRecipeExistsAndUserIsAuthor(id);
        recipesRepository.deleteById(id);
    }
    
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return usersRepository.findByEmail(userDetails.getUsername());
    }
    
    private void checkRecipeExistsAndUserIsAuthor(Long recipeId) {
        Recipe savedRecipe = recipesRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!savedRecipe.getUser().equals(getAuthenticatedUser())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
