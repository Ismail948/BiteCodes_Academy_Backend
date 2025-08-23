package academy.models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import academy.request_response.QuestionResult;


@Entity
public class ExamAttempt {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private Long userId; // Links to the user attempting the exam
//
//    @Column(nullable = false)
//    private String courseName; // Links to the course for which the attempt is made
//
//    @Column(nullable = false)
//    private double score; // Score of the attempt
//
//    @Column(nullable = false)
//    private boolean passed; // Indicates if the user passed or failed
//
//    @Column(nullable = false)
//    private LocalDateTime attemptedAt = LocalDateTime.now(); // Timestamp of the attempt
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long userId;
    private String courseName;
    private double score;
    private boolean passed;
    private String attemptedAt;
    private int timeTaken;
    private int correctAnswers;
    private int incorrectAnswers;
    private int skipped;
    @ElementCollection
    private List<QuestionResult> detailedResults;
    private int totalQuestions;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public String getAttemptedAt() {
		return attemptedAt;
	}

	public void setAttemptedAt(String attemptedAt) {
		this.attemptedAt = attemptedAt;
	}

	

	
	public ExamAttempt(Long id, long userId, String courseName, double score, boolean passed, String attemptedAt,
			int timeTaken, int correctAnswers, int incorrectAnswers, int skipped, List<QuestionResult> detailedResults,
			int totalQuestions) {
		super();
		this.id = id;
		this.userId = userId;
		this.courseName = courseName;
		this.score = score;
		this.passed = passed;
		this.attemptedAt = attemptedAt;
		this.timeTaken = timeTaken;
		this.correctAnswers = correctAnswers;
		this.incorrectAnswers = incorrectAnswers;
		this.skipped = skipped;
		this.detailedResults = detailedResults;
		this.totalQuestions = totalQuestions;
	}

	public int getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(int totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public int getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(int timeTaken) {
		this.timeTaken = timeTaken;
	}

	public int getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(int correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

	public int getIncorrectAnswers() {
		return incorrectAnswers;
	}

	public void setIncorrectAnswers(int incorrectAnswers) {
		this.incorrectAnswers = incorrectAnswers;
	}

	public int getSkipped() {
		return skipped;
	}

	public void setSkipped(int skipped) {
		this.skipped = skipped;
	}

	public List<QuestionResult> getDetailedResults() {
		return detailedResults;
	}

	public void setDetailedResults(List<QuestionResult> detailedResults) {
		this.detailedResults = detailedResults;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public ExamAttempt() {
	}

    
}