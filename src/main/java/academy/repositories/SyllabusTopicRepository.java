package academy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.models.SyllabusTopic;

public interface SyllabusTopicRepository extends JpaRepository<SyllabusTopic, Long> {
    List<SyllabusTopic> findByCourseName(String courseName);
}
