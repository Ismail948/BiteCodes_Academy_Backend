package academy.controllers;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import academy.models.Course;
import academy.models.EntranceExam;
import academy.models.University;
import academy.repositories.EntranceExamRepository;
import academy.repositories.UniversityRepository;
import academy.services.PurchaseService;
import academy.services.UniversityService;

@RestController
@RequestMapping("/api/universities")
@CrossOrigin(origins = "*") // Allow Next.js frontend
public class UniversityController {

	@Autowired
	private PurchaseService purchaseService;
	
    @Autowired
    private UniversityService universityService;
    
    @Autowired
    private UniversityRepository universityRepository;
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
    @GetMapping("/withPurchase/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUniversitiesWithPurchase(
            @PathVariable Long userId) {

        List<University> universities = universityRepository.findAll();

        List<Map<String, Object>> result = universities.stream().map(university -> {
            boolean purchased = purchaseService.hasUserBoughtAllCoursesOfUniversity(userId, university.getSlug());

            Map<String, Object> uniData = new HashMap<>();
            uniData.put("slug", university.getSlug());
            uniData.put("name", university.getName());
            uniData.put("description", university.getDescription());
            uniData.put("location", university.getLocation());
            uniData.put("ranking", university.getRanking());
            uniData.put("established", university.getEstablished());
            uniData.put("examsAccepted", university.getExamsAccepted());
            uniData.put("courses", university.getCourses());
            uniData.put("allCoursesPrice", university.getAllCoursesPrice());
            uniData.put("purchased", purchased);

            return uniData;
        }).toList();

        return ResponseEntity.ok(result);
    }
}