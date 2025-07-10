package academy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.models.ExamQuestion;

import java.util.List;
import java.util.Optional;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
    List<ExamQuestion> findByCourseName(String courseName);
    List<ExamQuestion> findByCourseNameAndTopic(String courseName, String topic);
    
    Optional<ExamQuestion> findById(Long id);

}
