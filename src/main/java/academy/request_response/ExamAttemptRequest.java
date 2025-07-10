package academy.request_response;

import java.util.List;
import java.util.Map;

public class ExamAttemptRequest {
    private Long userId;
    private String courseName;
    private Map<Long, String> answers;
    private int timeTaken;
    private List<Long> questionIds; // New field for all question IDs\
    

    public List<Long> getQuestionIds() {
		return questionIds;
	}

	public void setQuestionIds(List<Long> questionIds) {
		this.questionIds = questionIds;
	}

	public int getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(int timeTaken) {
		this.timeTaken = timeTaken;
	}

	public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    
	

	public ExamAttemptRequest(Long userId, String courseName, Map<Long, String> answers, int timeTaken,
			List<Long> questionIds) {
		super();
		this.userId = userId;
		this.courseName = courseName;
		this.answers = answers;
		this.timeTaken = timeTaken;
		this.questionIds = questionIds;
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
