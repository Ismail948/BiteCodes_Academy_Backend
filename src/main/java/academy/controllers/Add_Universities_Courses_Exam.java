package academy.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import academy.models.Course;
import academy.models.EntranceExam;
import academy.models.University;
import academy.repositories.CourseRepo;
import academy.repositories.EntranceExamRepository;
import academy.repositories.UniversityRepository;

@RestController
@RequestMapping("/add")
@CrossOrigin(origins = "*") // Allow Next.js frontend
public class Add_Universities_Courses_Exam {

	@Autowired
	private CourseRepo courseRepo;
	
	@Autowired
	private EntranceExamRepository entranceExamRepository;
	
	@Autowired
	private UniversityRepository universityRepository;
	
	
	
	
	@PostMapping("/universities")
	public ResponseEntity<?> addUniversities(@RequestBody List<University> universities) {
	    for (University u : universities) {
	        if (u.getCourses() != null) {
	            for (Course c : u.getCourses()) {
	                c.setUniversity(u);
	            }
	        }
	    }
	    List<University> saved = universityRepository.saveAll(universities);
	    return ResponseEntity.ok(saved);
	}

	
	@PostMapping("/courses")
	private ResponseEntity<String> addCourses(@RequestBody List<Course> courses){
		courseRepo.saveAll(courses);
		return ResponseEntity.ok("Added Courses");
		
	}
	@PostMapping("/exams")
	private ResponseEntity<String> addExams(@RequestBody List<EntranceExam> exams){
		entranceExamRepository.saveAll(exams);
		return ResponseEntity.ok("Added Exams");
		
	}
	
	
}
