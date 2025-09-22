package academy.request_response;

import java.util.List;
import java.util.Map;

public class TopicAnalysis {
    private Map<String, Double> topicAccuracies;
    private List<String> improvingTopics;
    private List<String> decliningTopics;
    private String strongestTopic;
    private String weakestTopic;
	public Map<String, Double> getTopicAccuracies() {
		return topicAccuracies;
	}
	public void setTopicAccuracies(Map<String, Double> topicAccuracies) {
		this.topicAccuracies = topicAccuracies;
	}
	public List<String> getImprovingTopics() {
		return improvingTopics;
	}
	public void setImprovingTopics(List<String> improvingTopics) {
		this.improvingTopics = improvingTopics;
	}
	public List<String> getDecliningTopics() {
		return decliningTopics;
	}
	public void setDecliningTopics(List<String> decliningTopics) {
		this.decliningTopics = decliningTopics;
	}
	public String getStrongestTopic() {
		return strongestTopic;
	}
	public void setStrongestTopic(String strongestTopic) {
		this.strongestTopic = strongestTopic;
	}
	public String getWeakestTopic() {
		return weakestTopic;
	}
	public void setWeakestTopic(String weakestTopic) {
		this.weakestTopic = weakestTopic;
	}
	public TopicAnalysis(Map<String, Double> topicAccuracies, List<String> improvingTopics,
			List<String> decliningTopics, String strongestTopic, String weakestTopic) {
		super();
		this.topicAccuracies = topicAccuracies;
		this.improvingTopics = improvingTopics;
		this.decliningTopics = decliningTopics;
		this.strongestTopic = strongestTopic;
		this.weakestTopic = weakestTopic;
	}
	public TopicAnalysis() {
		super();
		// TODO Auto-generated constructor stub
	}
    

    // Constructors, getters, setters...
}