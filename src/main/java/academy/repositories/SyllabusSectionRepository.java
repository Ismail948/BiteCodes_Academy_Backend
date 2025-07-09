package academy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.models.SyllabusSection;

public interface SyllabusSectionRepository extends JpaRepository<SyllabusSection, Long> {
}
