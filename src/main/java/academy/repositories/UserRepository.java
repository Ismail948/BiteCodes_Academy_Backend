package academy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.models.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);

}
