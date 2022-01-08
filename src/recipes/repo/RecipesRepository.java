package recipes.repo;

import org.springframework.data.repository.CrudRepository;
import recipes.entities.Recipe;

public interface RecipesRepository extends CrudRepository<Recipe, Long> {
    
}
