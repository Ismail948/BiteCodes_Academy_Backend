package academy.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import academy.models.EntranceExam;
import academy.services.EntranceExamService;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*")
public class EntranceExamController {

    @Autowired
    private EntranceExamService examService;

    @GetMapping
    public List<EntranceExam> getAllExams() {
        return examService.getAllExams();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntranceExam> getExamById(@PathVariable String id) {
        return examService.getExamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/featured")
    public List<EntranceExam> getFeaturedExams() {
        return examService.getFeaturedExams();
    }

    @PostMapping
    public List<EntranceExam> addEntranceExams(@RequestBody List<EntranceExam> obj) {
        return examService.addExams(obj);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExam(@PathVariable String id) {
        try {
            examService.deleteExam(id);
            return ResponseEntity.ok().body("Deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
