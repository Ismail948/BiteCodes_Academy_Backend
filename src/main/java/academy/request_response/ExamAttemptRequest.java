package academy.request_response;

import java.util.Map;

public class ExamAttemptRequest {
    private Long userId;
    private String courseName;
    private Map<Long, String> answers;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    
	public ExamAttemptRequest(Long userId, String courseName, Map<Long, String> answers) {
		super();
		this.userId = userId;
		this.courseName = courseName;
		this.answers = answers;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers;
    }
}
