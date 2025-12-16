package tn.esprit.spring.event.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.event.demo.Model.UserRegisterToken;
import java.util.Optional;

public interface UserRegisterTokenRepository extends JpaRepository<UserRegisterToken, Integer> {

    Optional<UserRegisterToken> findByToken(String token);

    void deleteByUser_Id(Long userId);
}
