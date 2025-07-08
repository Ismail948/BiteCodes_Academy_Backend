package academy.controllers;

import org.springframework.web.bind.annotation.*;

import academy.models.ExamAttempt;
import academy.models.ExamQuestion;
import academy.request_response.ExamAttemptRequest;
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
    public ExamAttempt submitExam(@RequestBody ExamAttemptRequest request) {
        double score = examQuestionService.evaluateExam(request.getAnswers());
        boolean passed = score >= 80; // Example passing criteria
        if(!(score>=0 && score<=100)) {
        	score=0;
        }
        score=Math.round(score);
        ExamAttempt attempt = new ExamAttempt(null, request.getUserId(), request.getCourseName(), score, passed, LocalDateTime.now());
        ExamAttempt savedAttempt = examAttemptService.saveAttempt(attempt);
        
        return savedAttempt; // Return the saved attempt with ID
    }

    @GetMapping("/result/{attemptId}")
    public ExamAttempt getExamResult(@PathVariable Long attemptId) {
        return examAttemptService.getAttemptById(attemptId).orElse(null);
    }
}

