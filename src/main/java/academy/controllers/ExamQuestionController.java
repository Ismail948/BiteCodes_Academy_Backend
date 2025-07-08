package academy.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import academy.models.ExamQuestion;
import academy.request_response.ExamQuestionRequest;
import academy.services.ExamQuestionService;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*") // Allow Next.js frontend
public class ExamQuestionController {

    private final ExamQuestionService examQuestionService;

    public ExamQuestionController(ExamQuestionService examQuestionService) {
        this.examQuestionService = examQuestionService;
    }

    @GetMapping("/course/{courseName}")
    public List<ExamQuestion> getQuestions(@PathVariable String courseName) {
        return examQuestionService.getQuestionsByCourse(courseName);
    }

    @PostMapping
    public ResponseEntity<String> addQuestion(@RequestBody List<ExamQuestion> question) {
        examQuestionService.addQuestions(question);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully added Questions");
    }

}

