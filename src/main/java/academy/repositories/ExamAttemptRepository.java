package academy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.models.ExamAttempt;

import java.util.List;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByUserId(Long userId);
    List<ExamAttempt> findByCourseName(String courseName);
}
