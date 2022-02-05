package recipes.repo;

import org.springframework.data.repository.CrudRepository;
import recipes.entities.User;

public interface UsersRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
}
