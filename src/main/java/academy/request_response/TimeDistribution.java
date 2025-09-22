package academy.request_response;

import java.util.List;
import java.util.Map;

//Missing TimeDistribution class
public class TimeDistribution {
 private double averageTimePerQuestion;
 private double totalTimeSpent;
 private Map<String, Double> timeByTopic;
 private Map<String, Double> timeByDifficulty;
 private List<String> fastestTopics;
 private List<String> slowestTopics;
 private double timeEfficiencyScore;
 private String timeManagementTip;

 public TimeDistribution() {}

 // Getters and setters
 public double getAverageTimePerQuestion() { return averageTimePerQuestion; }
 public void setAverageTimePerQuestion(double averageTimePerQuestion) { this.averageTimePerQuestion = averageTimePerQuestion; }

 public double getTotalTimeSpent() { return totalTimeSpent; }
 public void setTotalTimeSpent(double totalTimeSpent) { this.totalTimeSpent = totalTimeSpent; }

 public Map<String, Double> getTimeByTopic() { return timeByTopic; }
 public void setTimeByTopic(Map<String, Double> timeByTopic) { this.timeByTopic = timeByTopic; }

 public Map<String, Double> getTimeByDifficulty() { return timeByDifficulty; }
 public void setTimeByDifficulty(Map<String, Double> timeByDifficulty) { this.timeByDifficulty = timeByDifficulty; }

 public List<String> getFastestTopics() { return fastestTopics; }
 public void setFastestTopics(List<String> fastestTopics) { this.fastestTopics = fastestTopics; }

 public List<String> getSlowestTopics() { return slowestTopics; }
 public void setSlowestTopics(List<String> slowestTopics) { this.slowestTopics = slowestTopics; }

 public double getTimeEfficiencyScore() { return timeEfficiencyScore; }
 public void setTimeEfficiencyScore(double timeEfficiencyScore) { this.timeEfficiencyScore = timeEfficiencyScore; }

 public String getTimeManagementTip() { return timeManagementTip; }
 public void setTimeManagementTip(String timeManagementTip) { this.timeManagementTip = timeManagementTip; }
}