package academy.services;

import academy.models.Course;
import academy.models.EntranceExam;
import academy.models.University;
import academy.repositories.CourseRepo;
import academy.repositories.EntranceExamRepository;
import academy.repositories.UniversityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

@Service
public class UniversityService {

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private EntranceExamRepository entranceRepo;

    @Cacheable(value = "universities")
    public List<University> getAllUniversities() {
        return universityRepository.findAll();
    }

    @Cacheable(value = "university", key = "#slug")
    public Optional<University> getUniversityBySlug(String slug) {
        return universityRepository.findBySlug(slug);
    }

    @Cacheable(value = "courses", key = "#courseSlug")
    public Optional<Course> getCourseBySlug(String universitySlug, String courseSlug) {
        return courseRepo.findById(courseSlug);
    }

    @Cacheable(value = "exams", key = "#examSlug")
    public Optional<EntranceExam> getExamBySlug(String examSlug) {
        return entranceRepo.findById(examSlug);
    }

    // Optional: to evict cache when updating
    @CacheEvict(value = "universities", allEntries = true)
    public University saveUniversity(University uni) {
        return universityRepository.save(uni);
    }
}
