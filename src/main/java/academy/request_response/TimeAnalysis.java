package academy.request_response;

import java.util.List;

public class TimeAnalysis {
    private double averageTimePerQuestion;
    private List<String> fastAnsweredTopics;
    private List<String> slowAnsweredTopics;
    private double timeEfficiencyScore;
	public double getAverageTimePerQuestion() {
		return averageTimePerQuestion;
	}
	public void setAverageTimePerQuestion(double averageTimePerQuestion) {
		this.averageTimePerQuestion = averageTimePerQuestion;
	}
	public List<String> getFastAnsweredTopics() {
		return fastAnsweredTopics;
	}
	public void setFastAnsweredTopics(List<String> fastAnsweredTopics) {
		this.fastAnsweredTopics = fastAnsweredTopics;
	}
	public List<String> getSlowAnsweredTopics() {
		return slowAnsweredTopics;
	}
	public void setSlowAnsweredTopics(List<String> slowAnsweredTopics) {
		this.slowAnsweredTopics = slowAnsweredTopics;
	}
	public double getTimeEfficiencyScore() {
		return timeEfficiencyScore;
	}
	public void setTimeEfficiencyScore(double timeEfficiencyScore) {
		this.timeEfficiencyScore = timeEfficiencyScore;
	}
	public TimeAnalysis(double averageTimePerQuestion, List<String> fastAnsweredTopics, List<String> slowAnsweredTopics,
			double timeEfficiencyScore) {
		super();
		this.averageTimePerQuestion = averageTimePerQuestion;
		this.fastAnsweredTopics = fastAnsweredTopics;
		this.slowAnsweredTopics = slowAnsweredTopics;
		this.timeEfficiencyScore = timeEfficiencyScore;
	}
	public TimeAnalysis() {
		super();
		// TODO Auto-generated constructor stub
	}

    // Constructors, getters, setters...
}
