package academy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.models.ExamQuestion;

import java.util.List;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
    List<ExamQuestion> findByCourseName(String courseName);
}
