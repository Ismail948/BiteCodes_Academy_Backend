package academy.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import academy.models.Course;

public interface CourseRepo extends JpaRepository<Course, String> {
}
