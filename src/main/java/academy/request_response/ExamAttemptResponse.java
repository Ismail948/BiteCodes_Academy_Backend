package academy.request_response;

import java.time.LocalDateTime;
import java.util.List;

public class ExamAttemptResponse {
    private long id;
    private long userId;
    private String courseName;
    private double score;
    private boolean passed;
    private String attemptedAt;
    private int timeTaken;
    private int correctAnswers;
    private int incorrectAnswers;
    private int skipped;
    private List<QuestionResult> detailedResults;
    private int totalQuestions;



	public ExamAttemptResponse(long id, long userId, String courseName, double score, boolean passed,
			String attemptedAt, int timeTaken, int correctAnswers, int incorrectAnswers, int skipped,
			List<QuestionResult> detailedResults, int totalQuestions) {
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

	// Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
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
}
