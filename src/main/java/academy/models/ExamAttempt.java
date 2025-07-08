package academy.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; // Links to the user attempting the exam

    @Column(nullable = false)
    private String courseName; // Links to the course for which the attempt is made

    @Column(nullable = false)
    private double score; // Score of the attempt

    @Column(nullable = false)
    private boolean passed; // Indicates if the user passed or failed

    @Column(nullable = false)
    private LocalDateTime attemptedAt = LocalDateTime.now(); // Timestamp of the attempt

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

	public LocalDateTime getAttemptedAt() {
		return attemptedAt;
	}

	public void setAttemptedAt(LocalDateTime attemptedAt) {
		this.attemptedAt = attemptedAt;
	}

	
	

	public ExamAttempt(Long id, Long userId, String courseName, double score, boolean passed,
			LocalDateTime attemptedAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.courseName = courseName;
		this.score = score;
		this.passed = passed;
		this.attemptedAt = attemptedAt;
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