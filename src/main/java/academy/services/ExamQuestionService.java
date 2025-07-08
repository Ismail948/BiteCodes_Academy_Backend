package academy.services;

import org.springframework.stereotype.Service;

import academy.models.ExamQuestion;
import academy.repositories.ExamQuestionRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;

    public ExamQuestionService(ExamQuestionRepository examQuestionRepository) {
        this.examQuestionRepository = examQuestionRepository;
    }

    public List<ExamQuestion> getQuestionsByCourse(String courseName) {
        return examQuestionRepository.findByCourseName(courseName);
    }

    public ExamQuestion addQuestion(ExamQuestion question) {
        return examQuestionRepository.save(question);
}
    
    public void addQuestions(List<ExamQuestion> question) {
        examQuestionRepository.saveAll(question);
    }
    public List<ExamQuestion> getRandomQuestions(String courseName, int count) {
        List<ExamQuestion> questions = examQuestionRepository.findByCourseName(courseName);
        Collections.shuffle(questions);
        return questions.stream().limit(count).collect(Collectors.toList());
    }

//    public double evaluateExam(Map<Long, String> answers) {
//        int totalQuestions = answers.size();
//        long correctAnswers = answers.entrySet().stream()
//                .filter(entry -> {
//                    ExamQuestion question = examQuestionRepository.findById(entry.getKey()).orElse(null);
//                    return question != null && question.getCorrectOption().equals(entry.getValue());
//                })
//                .count();
//        return ((double) correctAnswers / totalQuestions) * 100;
//    } 
    
    public double evaluateExam(Map<Long, String> answers) {
        if (answers == null || answers.isEmpty()) {
            System.out.println("No answers provided, returning score 0");
            return 0.0;
        }

        int totalQuestions = answers.size();
        long correctAnswers = answers.entrySet().stream()
                .filter(entry -> {
                    ExamQuestion question = examQuestionRepository.findById(entry.getKey()).orElse(null);
                    if (question == null) {
                        System.out.println("Question not found for ID: " + entry.getKey());
                        return false;
                    }
                    boolean isCorrect = question.getCorrectOption().equals(entry.getValue());
                    System.out.println("Question ID: " + entry.getKey() +
                            ", Submitted: " + entry.getValue() +
                            ", Correct: " + question.getCorrectOption() +
                            ", Is Correct: " + isCorrect);
                    return isCorrect;
                })
                .count();

        double score = ((double) correctAnswers / totalQuestions) * 100;
        System.out.println("Correct Answers: " + correctAnswers + ", Total Questions: " + totalQuestions + ", Score: " + score);
        return score;
    }
}
