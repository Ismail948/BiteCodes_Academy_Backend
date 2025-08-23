package academy.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import academy.models.ExamAttempt;
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
            if (request.getUserId() <= 0) {
                return ResponseEntity.badRequest().body("Invalid user ID");
            }
            if (request.getCourseName() == null || request.getCourseName().isEmpty()) {
                return ResponseEntity.badRequest().body("Course name is required");
            }
            if (request.getQuestionIds() == null || request.getQuestionIds().isEmpty()) {
                return ResponseEntity.badRequest().body("Question IDs cannot be null or empty");
            }

            ExamAttemptResponse response = examQuestionService.evaluateExam(request);
            ExamAttempt attempt = new ExamAttempt(
                null,
                request.getUserId(),
                request.getCourseName(),
                response.getScore(),
                response.isPassed(),
                response.getAttemptedAt(),
                request.getTimeTaken(),
                response.getCorrectAnswers(),
                response.getIncorrectAnswers(),
                response.getSkipped(),
                response.getDetailedResults(),
                response.getTotalQuestions()
            );
            ExamAttempt savedAttempt = examAttemptService.saveAttempt(attempt);
            
            ExamAttemptResponse savedResponse = new ExamAttemptResponse(
                savedAttempt.getId(),
                savedAttempt.getUserId(),
                savedAttempt.getCourseName(),
                savedAttempt.getScore(),
                savedAttempt.isPassed(),
                savedAttempt.getAttemptedAt().toString(),
                savedAttempt.getTimeTaken(),
                savedAttempt.getCorrectAnswers(),
                savedAttempt.getIncorrectAnswers(),
                savedAttempt.getSkipped(),
                savedAttempt.getDetailedResults(),
                savedAttempt.getTotalQuestions()
            );
            
            return ResponseEntity.ok(savedResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing the exam submission");
        }
    }


    @GetMapping("/result/{attemptId}")
    public ExamAttempt getExamResult(@PathVariable Long attemptId) {
        return examAttemptService.getAttemptById(attemptId).orElse(null);
    }
}

