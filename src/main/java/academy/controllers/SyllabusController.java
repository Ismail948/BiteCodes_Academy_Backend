package academy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import academy.models.Syllabus;
import academy.repositories.SyllabusRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/syllabus")
public class SyllabusController {

    private final SyllabusRepository syllabusRepository;

    public SyllabusController(SyllabusRepository syllabusRepository) {
        this.syllabusRepository = syllabusRepository;
    }

    @PostMapping
    public ResponseEntity<Syllabus> addSyllabus(@RequestBody Syllabus syllabus) {
        // Automatically link each section with syllabus
        if (syllabus.getSections() != null) {
            syllabus.getSections().forEach(section -> section.setSyllabus(syllabus));
        }
        return ResponseEntity.ok(syllabusRepository.save(syllabus));
    }

    @GetMapping("/{courseName}")
    public ResponseEntity<Syllabus> getByCourseName(@PathVariable String courseName) {
        Optional<Syllabus> optional = syllabusRepository.findByCourseName(courseName);
        return optional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
