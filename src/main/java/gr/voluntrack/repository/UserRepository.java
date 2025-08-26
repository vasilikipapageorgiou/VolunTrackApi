package gr.voluntrack.repository;

import gr.voluntrack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);  // Αντί για username
    boolean existsByEmail(String email);
}
