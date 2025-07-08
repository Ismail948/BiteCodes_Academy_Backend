package academy.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import academy.models.EntranceExam;
import academy.repositories.EntranceExamRepository;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*")
public class EntranceExamController {
    
    @Autowired
    private EntranceExamRepository repo;

    @GetMapping
    public List<EntranceExam> getAllExams() {
    	System.out.print(repo.findAll());
        return repo.findAll();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExam(@PathVariable String id){
    	System.out.print(id);
    	try {
    		 repo.deleteById(id);
    		 return ResponseEntity.ok().body("Deleted successfully");
    	}
    	catch(Exception e) {
    		 return ResponseEntity.notFound().build();
    	}
    	
    }
    
    @PostMapping
    public List<EntranceExam> addEntranceExams(@RequestBody List<EntranceExam> obj) {
        return repo.saveAll(obj);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntranceExam> getExamById(@PathVariable String id) {
    	System.out.print("Hello");
    	return repo.findById(id)
                   .map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/featured")
    public List<EntranceExam> getFeaturedExams() {
        return repo.findByFeatured(true);
    }
}

