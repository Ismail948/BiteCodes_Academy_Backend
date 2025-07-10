package academy.models;

import javax.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Syllabus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String courseName; // e.g., "cmat", "gujcet"

    private Integer totalMarks;

    private Integer totalQuestions;

    private Double marksPerQuestion;
    
    private String syllabusLink;

    private Double negativeMarking; // e.g., 0.25 for -1/4

    @Column(length = 1000)
    private String generalInstructions;

    @OneToMany(mappedBy = "syllabus", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SyllabusSection> sections;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public Integer getTotalMarks() {
		return totalMarks;
	}

	public void setTotalMarks(Integer totalMarks) {
		this.totalMarks = totalMarks;
	}

	public Integer getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(Integer totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public Double getMarksPerQuestion() {
		return marksPerQuestion;
	}

	public void setMarksPerQuestion(Double marksPerQuestion) {
		this.marksPerQuestion = marksPerQuestion;
	}

	public Double getNegativeMarking() {
		return negativeMarking;
	}

	public void setNegativeMarking(Double negativeMarking) {
		this.negativeMarking = negativeMarking;
	}

	public String getGeneralInstructions() {
		return generalInstructions;
	}

	public void setGeneralInstructions(String generalInstructions) {
		this.generalInstructions = generalInstructions;
	}

	public List<SyllabusSection> getSections() {
		return sections;
	}

	public void setSections(List<SyllabusSection> sections) {
		this.sections = sections;
	}

	

	public Syllabus(Long id, String courseName, Integer totalMarks, Integer totalQuestions, Double marksPerQuestion,
			String syllabusLink, Double negativeMarking, String generalInstructions, List<SyllabusSection> sections) {
		super();
		this.id = id;
		this.courseName = courseName;
		this.totalMarks = totalMarks;
		this.totalQuestions = totalQuestions;
		this.marksPerQuestion = marksPerQuestion;
		this.syllabusLink = syllabusLink;
		this.negativeMarking = negativeMarking;
		this.generalInstructions = generalInstructions;
		this.sections = sections;
	}

	public String getSyllabusLink() {
		return syllabusLink;
	}

	public void setSyllabusLink(String syllabusLink) {
		this.syllabusLink = syllabusLink;
	}

	public Syllabus() {
	}

}

