package academy.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.models.Course;

public interface CourseRepo extends JpaRepository<Course, String> {
	 List<Course> findByUniversitySlug(String universitySlug);
}
