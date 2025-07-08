package academy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.models.EntranceExam;

public interface EntranceExamRepository extends JpaRepository<EntranceExam, String> {
    List<EntranceExam> findByFeatured(boolean featured);
}
