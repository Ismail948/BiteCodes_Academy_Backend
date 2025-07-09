package academy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.models.StudyMaterial;

public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {
    List<StudyMaterial> findByTopic_CourseName(String courseName);
}
