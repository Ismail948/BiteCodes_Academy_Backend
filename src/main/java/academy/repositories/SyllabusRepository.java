package academy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.models.Syllabus;

import java.util.Optional;

public interface SyllabusRepository extends JpaRepository<Syllabus, Long> {
    Optional<Syllabus> findByCourseName(String courseName);
}
