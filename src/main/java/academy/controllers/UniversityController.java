package academy.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import academy.models.Course;
import academy.models.EntranceExam;
import academy.models.University;
import academy.repositories.EntranceExamRepository;
import academy.services.UniversityService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/universities")
@CrossOrigin(origins = "*") // Allow Next.js frontend
public class UniversityController {

    @Autowired
    private UniversityService universityService;
    
    @Autowired
    private EntranceExamRepository entranceRepo;

    @GetMapping
    public List<University> getAllUniversities() {
        return universityService.getAllUniversities();
    }

    @GetMapping("/{universitySlug}")
    public ResponseEntity<University> getUniversityBySlug(@PathVariable String universitySlug) {
        Optional<University> university = universityService.getUniversityBySlug(universitySlug);
        return university.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{universitySlug}/{courseSlug}")
    public ResponseEntity<Course> getCourseBySlug(@PathVariable String universitySlug, @PathVariable String courseSlug) {
        Optional<Course> course = universityService.getCourseBySlug(universitySlug, courseSlug);
        return course.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/exams/{examSlug}")
    public ResponseEntity<Optional<EntranceExam>> getExamBySlug(@PathVariable String examSlug) {
        Optional<EntranceExam> exam = entranceRepo.findById(examSlug);
        return ResponseEntity.ok(exam);
        
    }
}