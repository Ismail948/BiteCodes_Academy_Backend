package academy.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public class SyllabusSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sectionName; // e.g., "Quantitative Aptitude"

    private Integer marks;

    private Integer numberOfQuestions;

    @Column(length = 2000)
    private String topicsCovered; // e.g., "Algebra, Geometry, Trigonometry"

    @ManyToOne
    @JoinColumn(name = "syllabus_id")
    @JsonBackReference
    private Syllabus syllabus;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public Integer getMarks() {
		return marks;
	}

	public void setMarks(Integer marks) {
		this.marks = marks;
	}

	public Integer getNumberOfQuestions() {
		return numberOfQuestions;
	}

	public void setNumberOfQuestions(Integer numberOfQuestions) {
		this.numberOfQuestions = numberOfQuestions;
	}

	public String getTopicsCovered() {
		return topicsCovered;
	}

	public void setTopicsCovered(String topicsCovered) {
		this.topicsCovered = topicsCovered;
	}

	public Syllabus getSyllabus() {
		return syllabus;
	}

	public void setSyllabus(Syllabus syllabus) {
		this.syllabus = syllabus;
	}

	public SyllabusSection(Long id, String sectionName, Integer marks, Integer numberOfQuestions, String topicsCovered,
			Syllabus syllabus) {
		super();
		this.id = id;
		this.sectionName = sectionName;
		this.marks = marks;
		this.numberOfQuestions = numberOfQuestions;
		this.topicsCovered = topicsCovered;
		this.syllabus = syllabus;
	}

	public SyllabusSection() {
	}

   
}

