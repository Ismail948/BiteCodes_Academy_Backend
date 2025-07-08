package academy.services;

import academy.models.Course;
import academy.models.EntranceExam;
import academy.models.University;
import academy.repositories.CourseRepo;
import academy.repositories.EntranceExamRepository;
import academy.repositories.UniversityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UniversityService {

	@Autowired
    private  UniversityRepository universityRepository;

    @Autowired
    private EntranceExamRepository entranceRepo;
    
    @Autowired
    private CourseRepo courseRepo;
    
    public List<University> getAllUniversities() {
        return universityRepository.findAll();
    }

    public Optional<University> getUniversityBySlug(String slug) {
        return universityRepository.findBySlug(slug);
    }

    public Optional<Course> getCourseBySlug(String universitySlug, String courseSlug) {
        return courseRepo.findById(courseSlug);
    }

    public Optional<EntranceExam> getExamBySlug(String examSlug) {
        return entranceRepo.findById(examSlug);
    }
}
