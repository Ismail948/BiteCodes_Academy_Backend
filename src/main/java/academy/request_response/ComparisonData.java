package academy.request_response;

import java.util.List;
import java.util.Map;

//Missing ComparisonData class
public class ComparisonData {
 private double averageScoreForCourse;
 private double userScorePercentile;
 private double averageTimeForCourse;
 private double userTimePercentile;
 private Map<String, Double> topicComparisonWithAverage;
 private String performanceRanking; // "Top 10%", "Above Average", "Below Average", etc.
 private List<String> betterThanAverageTopics;
 private List<String> belowAverageTopics;
 private String comparisonSummary;

 public ComparisonData() {}

 // Getters and setters
 public double getAverageScoreForCourse() { return averageScoreForCourse; }
 public void setAverageScoreForCourse(double averageScoreForCourse) { this.averageScoreForCourse = averageScoreForCourse; }

 public double getUserScorePercentile() { return userScorePercentile; }
 public void setUserScorePercentile(double userScorePercentile) { this.userScorePercentile = userScorePercentile; }

 public double getAverageTimeForCourse() { return averageTimeForCourse; }
 public void setAverageTimeForCourse(double averageTimeForCourse) { this.averageTimeForCourse = averageTimeForCourse; }

 public double getUserTimePercentile() { return userTimePercentile; }
 public void setUserTimePercentile(double userTimePercentile) { this.userTimePercentile = userTimePercentile; }

 public Map<String, Double> getTopicComparisonWithAverage() { return topicComparisonWithAverage; }
 public void setTopicComparisonWithAverage(Map<String, Double> topicComparisonWithAverage) { 
     this.topicComparisonWithAverage = topicComparisonWithAverage; 
 }

 public String getPerformanceRanking() { return performanceRanking; }
 public void setPerformanceRanking(String performanceRanking) { this.performanceRanking = performanceRanking; }

 public List<String> getBetterThanAverageTopics() { return betterThanAverageTopics; }
 public void setBetterThanAverageTopics(List<String> betterThanAverageTopics) { 
     this.betterThanAverageTopics = betterThanAverageTopics; 
 }

 public List<String> getBelowAverageTopics() { return belowAverageTopics; }
 public void setBelowAverageTopics(List<String> belowAverageTopics) { this.belowAverageTopics = belowAverageTopics; }

 public String getComparisonSummary() { return comparisonSummary; }
 public void setComparisonSummary(String comparisonSummary) { this.comparisonSummary = comparisonSummary; }
}