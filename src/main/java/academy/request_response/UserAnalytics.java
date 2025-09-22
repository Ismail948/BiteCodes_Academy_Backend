package academy.request_response;

import java.util.List;
import java.util.Map;

import academy.models.TopicPerformance;

//Supporting DTOs
public class UserAnalytics {
 private int totalAttempts;
 private double averageScore;
 private int totalCorrectAnswers;
 private int totalQuestions;
 private double overallAccuracy;
 private Map<String, TopicPerformance> topicWisePerformance;
 private List<String> strongTopics;
 private List<String> weakTopics;
 private Map<String, Double> courseWiseScores;
 private ProgressData progressData;
 private String aiInsights;

 // Constructors, getters, setters...
 public UserAnalytics() {}

 public int getTotalAttempts() { return totalAttempts; }
 public void setTotalAttempts(int totalAttempts) { this.totalAttempts = totalAttempts; }

 public double getAverageScore() { return averageScore; }
 public void setAverageScore(double averageScore) { this.averageScore = averageScore; }

 public int getTotalCorrectAnswers() { return totalCorrectAnswers; }
 public void setTotalCorrectAnswers(int totalCorrectAnswers) { this.totalCorrectAnswers = totalCorrectAnswers; }

 public int getTotalQuestions() { return totalQuestions; }
 public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

 public double getOverallAccuracy() { return overallAccuracy; }
 public void setOverallAccuracy(double overallAccuracy) { this.overallAccuracy = overallAccuracy; }

 public Map<String, TopicPerformance> getTopicWisePerformance() { return topicWisePerformance; }
 public void setTopicWisePerformance(Map<String, TopicPerformance> topicWisePerformance) { 
     this.topicWisePerformance = topicWisePerformance; 
 }

 public List<String> getStrongTopics() { return strongTopics; }
 public void setStrongTopics(List<String> strongTopics) { this.strongTopics = strongTopics; }

 public List<String> getWeakTopics() { return weakTopics; }
 public void setWeakTopics(List<String> weakTopics) { this.weakTopics = weakTopics; }

 public Map<String, Double> getCourseWiseScores() { return courseWiseScores; }
 public void setCourseWiseScores(Map<String, Double> courseWiseScores) { this.courseWiseScores = courseWiseScores; }

 public ProgressData getProgressData() { return progressData; }
 public void setProgressData(ProgressData progressData) { this.progressData = progressData; }

 public String getAiInsights() { return aiInsights; }
 public void setAiInsights(String aiInsights) { this.aiInsights = aiInsights; }
}
