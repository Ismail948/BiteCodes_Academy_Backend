package academy.controllers;

import academy.models.PreviousPaper;
import academy.repositories.PreviousPaperRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/previous-papers")
@CrossOrigin(origins = "*") // Allow Next.js frontend
public class PreviousPaperController {

    private final PreviousPaperRepository paperRepo;

    public PreviousPaperController(PreviousPaperRepository paperRepo) {
        this.paperRepo = paperRepo;
    }

    @PostMapping
    public ResponseEntity<PreviousPaper> addPaper(@RequestBody PreviousPaper paper) {
        return ResponseEntity.ok(paperRepo.save(paper));
    }
    
    @PostMapping("/bulk")
    public ResponseEntity<List<PreviousPaper>> addPaperBulk(@RequestBody List<PreviousPaper> paper) {
        return ResponseEntity.ok(paperRepo.saveAll(paper));
    }


    @GetMapping("/{examSlug}")
    public ResponseEntity<List<PreviousPaper>> getPapersByExam(@PathVariable String examSlug) {
    	System.err.println("phdiehdwe    :"+examSlug);
        return ResponseEntity.ok(paperRepo.findByExamSlug(examSlug));
    }
}
