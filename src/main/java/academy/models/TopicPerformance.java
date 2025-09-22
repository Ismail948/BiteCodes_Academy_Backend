package academy.models;

import jakarta.persistence.Embeddable;

@Embeddable
public class TopicPerformance {
 private String topicName;
 private int totalQuestions;
 private int correctAnswers;
 private int incorrectAnswers;
 private double accuracy;
 private String difficultyLevel;
 
 public TopicPerformance() {}
 
 public TopicPerformance(String topicName, int totalQuestions, int correctAnswers, int incorrectAnswers) {
     this.topicName = topicName;
     this.totalQuestions = totalQuestions;
     this.correctAnswers = correctAnswers;
     this.incorrectAnswers = incorrectAnswers;
     this.accuracy = totalQuestions > 0 ? ((double) correctAnswers / totalQuestions) * 100 : 0.0;
 }
 
 // Getters and setters...
 public String getTopicName() { return topicName; }
 public void setTopicName(String topicName) { this.topicName = topicName; }
 
 public int getTotalQuestions() { return totalQuestions; }
 public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
 
 public int getCorrectAnswers() { return correctAnswers; }
 public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }
 
 public int getIncorrectAnswers() { return incorrectAnswers; }
 public void setIncorrectAnswers(int incorrectAnswers) { this.incorrectAnswers = incorrectAnswers; }
 
 public double getAccuracy() { return accuracy; }
 public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
 
 public String getDifficultyLevel() { return difficultyLevel; }
 public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
}
