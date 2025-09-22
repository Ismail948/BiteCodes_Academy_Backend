package academy.models;

import java.util.List;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ExamAttempt {
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
 private int totalQuestions;
 
 // New Analytics Fields
 private double cutoffScore = 80.0; // Default cutoff
 private double percentile; // Percentile rank among all attempts
 private String difficulty; // easy, medium, hard
 
 // Topic-wise Performance
 @ElementCollection
 @CollectionTable(name = "topic_performance")
 private Map<String, TopicPerformance> topicPerformance;
 
 // AI Analysis
 @Column(columnDefinition = "TEXT")
 private String aiAnalysis;
 
 @Column(columnDefinition = "TEXT")
 private String improvementSuggestions;
 
 @ElementCollection
 private List<String> weakTopics;
 
 @ElementCollection
 private List<String> strongTopics;
 
 @ElementCollection
 private List<QuestionResult> detailedResults;

public Long getId() {
	return id;
}

public void setId(Long id) {
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

public int getTotalQuestions() {
	return totalQuestions;
}

public void setTotalQuestions(int totalQuestions) {
	this.totalQuestions = totalQuestions;
}

public double getCutoffScore() {
	return cutoffScore;
}

public void setCutoffScore(double cutoffScore) {
	this.cutoffScore = cutoffScore;
}

public double getPercentile() {
	return percentile;
}

public void setPercentile(double percentile) {
	this.percentile = percentile;
}

public String getDifficulty() {
	return difficulty;
}

public void setDifficulty(String difficulty) {
	this.difficulty = difficulty;
}

public Map<String, TopicPerformance> getTopicPerformance() {
	return topicPerformance;
}

public void setTopicPerformance(Map<String, TopicPerformance> topicPerformance) {
	this.topicPerformance = topicPerformance;
}

public String getAiAnalysis() {
	return aiAnalysis;
}

public void setAiAnalysis(String aiAnalysis) {
	this.aiAnalysis = aiAnalysis;
}

public String getImprovementSuggestions() {
	return improvementSuggestions;
}

public void setImprovementSuggestions(String improvementSuggestions) {
	this.improvementSuggestions = improvementSuggestions;
}

public List<String> getWeakTopics() {
	return weakTopics;
}

public void setWeakTopics(List<String> weakTopics) {
	this.weakTopics = weakTopics;
}

public List<String> getStrongTopics() {
	return strongTopics;
}

public void setStrongTopics(List<String> strongTopics) {
	this.strongTopics = strongTopics;
}

public List<QuestionResult> getDetailedResults() {
	return detailedResults;
}

public void setDetailedResults(List<QuestionResult> detailedResults) {
	this.detailedResults = detailedResults;
}

public ExamAttempt(Long id, long userId, String courseName, double score, boolean passed, String attemptedAt,
		int timeTaken, int correctAnswers, int incorrectAnswers, int skipped, int totalQuestions, double cutoffScore,
		double percentile, String difficulty, Map<String, TopicPerformance> topicPerformance, String aiAnalysis,
		String improvementSuggestions, List<String> weakTopics, List<String> strongTopics,
		List<QuestionResult> detailedResults) {
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
	this.totalQuestions = totalQuestions;
	this.cutoffScore = cutoffScore;
	this.percentile = percentile;
	this.difficulty = difficulty;
	this.topicPerformance = topicPerformance;
	this.aiAnalysis = aiAnalysis;
	this.improvementSuggestions = improvementSuggestions;
	this.weakTopics = weakTopics;
	this.strongTopics = strongTopics;
	this.detailedResults = detailedResults;
}

public ExamAttempt() {
	super();
	// TODO Auto-generated constructor stub
}
 
 
}
