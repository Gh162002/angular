package tn.esprit.spring.event.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.event.demo.Model.PasswordResetToken;


public interface PasswordResetTokenRepository  extends JpaRepository<PasswordResetToken, Integer>{

    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUserId(Long userId);
}
