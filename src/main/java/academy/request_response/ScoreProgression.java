package academy.request_response;

import java.util.List;

//Additional supporting classes
public class ScoreProgression {
 private List<Double> scores;
 private double trend; // positive for improving, negative for declining
 private double bestScore;
 private double averageScore;
 private int attemptCount;
public List<Double> getScores() {
	return scores;
}
public void setScores(List<Double> scores) {
	this.scores = scores;
}
public double getTrend() {
	return trend;
}
public void setTrend(double trend) {
	this.trend = trend;
}
public double getBestScore() {
	return bestScore;
}
public void setBestScore(double bestScore) {
	this.bestScore = bestScore;
}
public double getAverageScore() {
	return averageScore;
}
public void setAverageScore(double averageScore) {
	this.averageScore = averageScore;
}
public int getAttemptCount() {
	return attemptCount;
}
public void setAttemptCount(int attemptCount) {
	this.attemptCount = attemptCount;
}
public ScoreProgression() {
	super();
	// TODO Auto-generated constructor stub
}
public ScoreProgression(List<Double> scores, double trend, double bestScore, double averageScore, int attemptCount) {
	super();
	this.scores = scores;
	this.trend = trend;
	this.bestScore = bestScore;
	this.averageScore = averageScore;
	this.attemptCount = attemptCount;
}

 // Constructors, getters, setters...
}