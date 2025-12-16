package tn.esprit.spring.event.demo.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.event.demo.Model.User;


import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User>findByEmail(String email);


}