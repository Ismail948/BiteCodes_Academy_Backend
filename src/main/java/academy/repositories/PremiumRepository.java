package academy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import academy.models.Premium;
import academy.models.User;

@Repository
public interface PremiumRepository extends JpaRepository<Premium, Long> {
    Optional<Premium> findByUserAndIsActiveTrue(User user);
    List<Premium> findByUserOrderByCreatedAtDesc(User user);
    List<Premium> findByIsActiveTrueAndEndDateBefore(java.time.LocalDateTime date);
}