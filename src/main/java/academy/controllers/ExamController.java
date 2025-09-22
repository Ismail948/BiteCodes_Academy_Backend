package academy.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import academy.models.ExamAttempt;
import academy.models.QuestionResult; // Add this import
import academy.models.ExamQuestion;
import academy.request_response.ExamAttemptRequest;
import academy.request_response.ExamAttemptResponse;
import academy.services.ExamAttemptService;
import academy.services.ExamQuestionService;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*") // Allow Next.js frontend
public class ExamController {
    
    private final ExamQuestionService examQuestionService;
    private final ExamAttemptService examAttemptService;
    
    public ExamController(ExamQuestionService examQuestionService, ExamAttemptService examAttemptService) {
        this.examQuestionService = examQuestionService;
        this.examAttemptService = examAttemptService;
    }
    
    @GetMapping("/course/{courseId}/random")
    public List<ExamQuestion> getRandomQuestions(@PathVariable String courseId) {
        return examQuestionService.getRandomQuestions(courseId, 5);
    }
    
    @PostMapping("/submit")
    public ResponseEntity<?> submitExam(@RequestBody ExamAttemptRequest request) {
        try {
            // Validation
            if (request.getUserId() <= 0) {
                return ResponseEntity.badRequest().body("Invalid user ID");
            }
            if (request.getCourseName() == null || request.getCourseName().isEmpty()) {
                return ResponseEntity.badRequest().body("Course name is required");
            }
            if (request.getQuestionIds() == null || request.getQuestionIds().isEmpty()) {
                return ResponseEntity.badRequest().body("Question IDs cannot be null or empty");
            }
            
            // Evaluate exam - this now returns a complete response with analytics
            ExamAttemptResponse response = examQuestionService.evaluateExam(request);
            
            // The ExamAttemptResponse already contains the saved attempt ID and all analytics
            // No need to create and save a separate ExamAttempt object
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing the exam submission");
        }
    }
    
    @GetMapping("/result/{attemptId}")
    public ResponseEntity<?> getExamResult(@PathVariable Long attemptId) {
        try {
            return examAttemptService.getAttemptById(attemptId)
                .map(attempt -> ResponseEntity.ok(convertToResponse(attempt)))
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving exam result");
        }
    }
    
    @GetMapping("/user/{userId}/attempts")
    public ResponseEntity<List<ExamAttemptResponse>> getUserAttempts(@PathVariable Long userId) {
        try {
            List<ExamAttempt> attempts = examAttemptService.getAttemptsByUserId(userId);
            List<ExamAttemptResponse> responses = attempts.stream()
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/course/{courseName}/attempts")
    public ResponseEntity<List<ExamAttemptResponse>> getCourseAttempts(@PathVariable String courseName) {
        try {
            List<ExamAttempt> attempts = examAttemptService.getAttemptsByCourseName(courseName);
            List<ExamAttemptResponse> responses = attempts.stream()
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private ExamAttemptResponse convertToResponse(ExamAttempt attempt) {
        return new ExamAttemptResponse(
            attempt.getId(),
            attempt.getUserId(),
            attempt.getCourseName(),
            attempt.getScore(),
            attempt.isPassed(),
            attempt.getAttemptedAt(),
            attempt.getTimeTaken(),
            attempt.getCorrectAnswers(),
            attempt.getIncorrectAnswers(),
            attempt.getSkipped(),
            attempt.getDetailedResults(),
            attempt.getTotalQuestions(),
            attempt.getTopicPerformance(),
            attempt.getAiAnalysis(),
            attempt.getImprovementSuggestions(),
            attempt.getWeakTopics(),
            attempt.getStrongTopics(),
            attempt.getPercentile(),
            attempt.getCutoffScore()
        );
    }
}