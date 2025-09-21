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
import academy.services.PremiumService;
import academy.services.UniversityService;

@RestController
@RequestMapping("/api/universities")
@CrossOrigin(origins = "*")
public class UniversityController {
    
    @Autowired
    private PremiumService premiumService;
    
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

    // Updated endpoint to check premium access instead of individual course purchases
    @GetMapping("/withAccess/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUniversitiesWithAccess(@PathVariable Long userId) {
        List<University> universities = universityRepository.findAll();
        
        // Check if user has premium subscription
        boolean hasPremium = premiumService.userHasPremium(userId);
        Map<String, Object> premiumStatus = premiumService.getPremiumStatus(userId);
        
        List<Map<String, Object>> result = universities.stream().map(university -> {
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
            
            // Premium users get access to all content
            uniData.put("hasAccess", hasPremium);
            uniData.put("accessType", hasPremium ? "premium" : "free");
            
            return uniData;
        }).toList();
        
        // Add premium status to response
        Map<String, Object> response = new HashMap<>();
        response.put("universities", result);
        response.put("premiumStatus", premiumStatus);
        response.put("totalUniversities", universities.size());
        
        return ResponseEntity.ok(result);
    }

    // New endpoint to get premium-specific university data
    @GetMapping("/premium/{userId}")
    public ResponseEntity<Map<String, Object>> getPremiumUniversityData(@PathVariable Long userId) {
        try {
            Map<String, Object> premiumStatus = premiumService.getPremiumStatus(userId);
            boolean hasPremium = (Boolean) premiumStatus.get("hasPremium");
            
            if (!hasPremium) {
                return ResponseEntity.ok(Map.of(
                        "hasAccess", false,
                        "message", "Premium subscription required for full access",
                        "premiumStatus", premiumStatus
                ));
            }
            
            List<University> universities = universityRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasAccess", true);
            response.put("universities", universities);
            response.put("premiumStatus", premiumStatus);
            response.put("totalCourses", universities.stream().mapToInt(u -> u.getCourses().size()).sum());
            response.put("message", "Full access granted - Premium subscriber");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to fetch premium university data: " + e.getMessage()
            ));
        }
    }

    // Get course access status for premium users
    @GetMapping("/{universitySlug}/{courseSlug}/access/{userId}")
    public ResponseEntity<Map<String, Object>> getCourseAccessStatus(
            @PathVariable String universitySlug,
            @PathVariable String courseSlug,
            @PathVariable Long userId) {
        
        try {
            Optional<Course> course = universityService.getCourseBySlug(universitySlug, courseSlug);
            
            if (course.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            boolean hasPremium = premiumService.userHasPremium(userId);
            Map<String, Object> premiumStatus = premiumService.getPremiumStatus(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("course", course.get());
            response.put("hasAccess", hasPremium);
            response.put("accessType", hasPremium ? "premium" : "free");
            response.put("premiumRequired", !hasPremium);
            response.put("premiumStatus", premiumStatus);
            
            if (!hasPremium) {
                response.put("message", "Premium subscription required to access this course");
                response.put("upgradeMessage", "Upgrade to Premium to unlock all courses and content");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to check course access: " + e.getMessage()
            ));
        }
    }
}