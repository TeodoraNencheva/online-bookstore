package bg.softuni.onlinebookstore.repositories;

import bg.softuni.onlinebookstore.model.entity.SecureTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecureTokenRepository extends JpaRepository<SecureTokenEntity, Long> {
    Optional<SecureTokenEntity> findByToken(String token);
}
