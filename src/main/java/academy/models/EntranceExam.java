package academy.models;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EntranceExam {
	@Id
	private String id;
	private String name;
	private String fullName;
	private String description;
	private String conductedBy;
	private String mode;
	private String difficulty;
	private String studentsAppear;
	private LocalDate examDate;
	private LocalDate applicationDeadline;

	// Additional fields from ExamData interface
    @ElementCollection
	private List<String> subjects;
	private Boolean featured;
	private String duration;

	// Exam Pattern
	private Integer totalQuestions;
	private Integer totalMarks;
	private String examPatternDuration;

	// Eligibility
	private String ageLimit;
	private String qualification;
	private String minMarks;

	// Fees
	private Integer generalFees;
	private Integer scFees;

	// Syllabus topics (simplified as strings)
    @ElementCollection
	private List<String> physicsTopics;
    @ElementCollection
	private List<String> chemistryTopics;
    @ElementCollection
	private List<String> mathematicsTopics;

	// Preparation Tips
    @ElementCollection
	private List<String> preparationTips;
    

	

	public EntranceExam(String id, String name, String fullName, String description, String conductedBy, String mode,
			String difficulty, String studentsAppear, LocalDate examDate, LocalDate applicationDeadline,
			List<String> subjects, Boolean featured, String duration, Integer totalQuestions, Integer totalMarks,
			String examPatternDuration, String ageLimit, String qualification, String minMarks, Integer generalFees,
			Integer scFees, List<String> physicsTopics, List<String> chemistryTopics, List<String> mathematicsTopics,
			List<String> preparationTips) {
		this.id = id;
		this.name = name;
		this.fullName = fullName;
		this.description = description;
		this.conductedBy = conductedBy;
		this.mode = mode;
		this.difficulty = difficulty;
		this.studentsAppear = studentsAppear;
		this.examDate = examDate;
		this.applicationDeadline = applicationDeadline;
		this.subjects = subjects;
		this.featured = featured;
		this.duration = duration;
		this.totalQuestions = totalQuestions;
		this.totalMarks = totalMarks;
		this.examPatternDuration = examPatternDuration;
		this.ageLimit = ageLimit;
		this.qualification = qualification;
		this.minMarks = minMarks;
		this.generalFees = generalFees;
		this.scFees = scFees;
		this.physicsTopics = physicsTopics;
		this.chemistryTopics = chemistryTopics;
		this.mathematicsTopics = mathematicsTopics;
		this.preparationTips = preparationTips;
	}




	public String getId() {
		return id;
	}




	public void setId(String id) {
		this.id = id;
	}




	public String getName() {
		return name;
	}




	public void setName(String name) {
		this.name = name;
	}




	public String getFullName() {
		return fullName;
	}




	public void setFullName(String fullName) {
		this.fullName = fullName;
	}




	public String getDescription() {
		return description;
	}




	public void setDescription(String description) {
		this.description = description;
	}




	public String getConductedBy() {
		return conductedBy;
	}




	public void setConductedBy(String conductedBy) {
		this.conductedBy = conductedBy;
	}




	public String getMode() {
		return mode;
	}




	public void setMode(String mode) {
		this.mode = mode;
	}




	public String getDifficulty() {
		return difficulty;
	}




	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}




	public String getStudentsAppear() {
		return studentsAppear;
	}




	public void setStudentsAppear(String studentsAppear) {
		this.studentsAppear = studentsAppear;
	}




	public LocalDate getExamDate() {
		return examDate;
	}




	public void setExamDate(LocalDate examDate) {
		this.examDate = examDate;
	}




	public LocalDate getApplicationDeadline() {
		return applicationDeadline;
	}




	public void setApplicationDeadline(LocalDate applicationDeadline) {
		this.applicationDeadline = applicationDeadline;
	}




	public List<String> getSubjects() {
		return subjects;
	}




	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}




	public Boolean getFeatured() {
		return featured;
	}




	public void setFeatured(Boolean featured) {
		this.featured = featured;
	}




	public String getDuration() {
		return duration;
	}




	public void setDuration(String duration) {
		this.duration = duration;
	}




	public Integer getTotalQuestions() {
		return totalQuestions;
	}




	public void setTotalQuestions(Integer totalQuestions) {
		this.totalQuestions = totalQuestions;
	}




	public Integer getTotalMarks() {
		return totalMarks;
	}




	public void setTotalMarks(Integer totalMarks) {
		this.totalMarks = totalMarks;
	}




	public String getExamPatternDuration() {
		return examPatternDuration;
	}




	public void setExamPatternDuration(String examPatternDuration) {
		this.examPatternDuration = examPatternDuration;
	}




	public String getAgeLimit() {
		return ageLimit;
	}




	public void setAgeLimit(String ageLimit) {
		this.ageLimit = ageLimit;
	}




	public String getQualification() {
		return qualification;
	}




	public void setQualification(String qualification) {
		this.qualification = qualification;
	}




	public String getMinMarks() {
		return minMarks;
	}




	public void setMinMarks(String minMarks) {
		this.minMarks = minMarks;
	}




	public Integer getGeneralFees() {
		return generalFees;
	}




	public void setGeneralFees(Integer generalFees) {
		this.generalFees = generalFees;
	}




	public Integer getScFees() {
		return scFees;
	}




	public void setScFees(Integer scFees) {
		this.scFees = scFees;
	}




	public List<String> getPhysicsTopics() {
		return physicsTopics;
	}




	public void setPhysicsTopics(List<String> physicsTopics) {
		this.physicsTopics = physicsTopics;
	}




	public List<String> getChemistryTopics() {
		return chemistryTopics;
	}




	public void setChemistryTopics(List<String> chemistryTopics) {
		this.chemistryTopics = chemistryTopics;
	}




	public List<String> getMathematicsTopics() {
		return mathematicsTopics;
	}




	public void setMathematicsTopics(List<String> mathematicsTopics) {
		this.mathematicsTopics = mathematicsTopics;
	}




	public List<String> getPreparationTips() {
		return preparationTips;
	}




	public void setPreparationTips(List<String> preparationTips) {
		this.preparationTips = preparationTips;
	}




	public EntranceExam() {
	}
   
    
    
}
