package academy.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import academy.models.ExamQuestion;
import academy.request_response.ExamQuestionRequest;
import academy.request_response.SectionWiseQuestionResponse;
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

    @GetMapping("/course/{courseName}/random")
    public ResponseEntity<List<SectionWiseQuestionResponse>> getRandomQuestions(@PathVariable String courseName) {
        List<SectionWiseQuestionResponse> sectionWiseQuestions = examQuestionService.getRandomQuestionsBySyllabus(courseName);
        if (sectionWiseQuestions == null || sectionWiseQuestions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sectionWiseQuestions);
    }


    @PostMapping
    public ResponseEntity<String> addQuestion(@RequestBody List<ExamQuestion> question) {
        examQuestionService.addQuestions(question);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully added Questions");
    }

}

