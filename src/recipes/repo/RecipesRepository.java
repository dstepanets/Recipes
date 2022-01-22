package recipes.repo;

import org.springframework.data.repository.CrudRepository;
import recipes.entities.Recipe;

import java.util.List;

public interface RecipesRepository extends CrudRepository<Recipe, Long> {
    List<Recipe> findAllByCategoryIgnoreCaseOrderByDateDesc(String category);

    List<Recipe> findAllByNameContainingIgnoreCaseOrderByDateDesc(String name);
}
