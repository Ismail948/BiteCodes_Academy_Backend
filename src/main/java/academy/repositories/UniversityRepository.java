package academy.repositories;


import academy.models.EntranceExam;
import academy.models.University;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityRepository  extends JpaRepository<University, String>{
    Optional<University> findBySlug(String slug);
}
