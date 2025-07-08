package academy.repositories;

import academy.models.PreviousPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PreviousPaperRepository extends JpaRepository<PreviousPaper, String> {
    List<PreviousPaper> findByExamSlug(String examSlug);
}
