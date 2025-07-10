package academy.services;

import org.springframework.stereotype.Service;

import academy.models.ExamQuestion;
import academy.models.Syllabus;
import academy.models.SyllabusSection;
import academy.repositories.ExamQuestionRepository;
import academy.repositories.SyllabusRepository;
import academy.request_response.ExamAttemptRequest;
import academy.request_response.ExamAttemptResponse;
import academy.request_response.QuestionResult;
import academy.request_response.SectionWiseQuestionResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;


    private final SyllabusRepository syllabusRepository;

    public ExamQuestionService(ExamQuestionRepository examQuestionRepository,SyllabusRepository syllabusRepository) {
        this.examQuestionRepository = examQuestionRepository;
        this.syllabusRepository=syllabusRepository;
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

    public List<SectionWiseQuestionResponse> getRandomQuestionsBySyllabus(String courseName) {
        Optional<Syllabus> optionalSyllabus = syllabusRepository.findByCourseName(courseName);
        if (optionalSyllabus.isPresent()) {
            return Collections.emptyList();
        }

        Syllabus syllabus = optionalSyllabus.get();
        List<SectionWiseQuestionResponse> result = new ArrayList<>();

        for (SyllabusSection section : syllabus.getSections()) {
            String topic = section.getSectionName();
            int questionCount = section.getNumberOfQuestions();

            List<ExamQuestion> allTopicQuestions = examQuestionRepository.findByCourseNameAndTopic(courseName, topic);
            Collections.shuffle(allTopicQuestions);

            List<ExamQuestion> selectedQuestions = allTopicQuestions.stream()
                    .limit(questionCount)
                    .collect(Collectors.toList());

            result.add(new SectionWiseQuestionResponse(topic, selectedQuestions));
        }

        return result;
    }


//    
//    public double evaluateExam(Map<Long, String> answers) {
//        if (answers == null || answers.isEmpty()) {
//            System.out.println("No answers provided, returning score 0");
//            return 0.0;
//        }
//
//        int totalQuestions = answers.size();
//        long correctAnswers = answers.entrySet().stream()
//                .filter(entry -> {
//                    ExamQuestion question = examQuestionRepository.findById(entry.getKey()).orElse(null);
//                    if (question == null) {
//                        System.out.println("Question not found for ID: " + entry.getKey());
//                        return false;
//                    }
//                    boolean isCorrect = question.getCorrectOption().equals(entry.getValue());
//                    System.out.println("Question ID: " + entry.getKey() +
//                            ", Submitted: " + entry.getValue() +
//                            ", Correct: " + question.getCorrectOption() +
//                            ", Is Correct: " + isCorrect);
//                    return isCorrect;
//                })
//                .count();
//
//        double score = ((double) correctAnswers / totalQuestions) * 100;
//        System.out.println("Correct Answers: " + correctAnswers + ", Total Questions: " + totalQuestions + ", Score: " + score);
//        return score;
//    }
    public ExamAttemptResponse evaluateExam(ExamAttemptRequest request) {
        String courseName = request.getCourseName();
        Map<Long, String> answers =
        	    request.getAnswers() != null
        	        ? request.getAnswers()
        	        : Collections.emptyMap();

        	List<Long> questionIds =
        	    request.getQuestionIds() != null
        	        ? request.getQuestionIds()
        	        : Collections.emptyList();

        // Validate questionIds
        if (questionIds.isEmpty()) {
            throw new IllegalArgumentException("No question IDs provided in the request");
        }
        int totalQuestions = questionIds.size();
        Set<Long> validQuestionIds = questionIds.stream().collect(Collectors.toSet());

        List<QuestionResult> detailedResults = new ArrayList<>();
        int correctAnswers = 0;
        int incorrectAnswers = 0;
        int skipped = 0;

        // Process submitted answers
        for (Map.Entry<Long, String> entry : answers.entrySet()) {
            Long questionId = entry.getKey();
            String userAnswer = entry.getValue();

            // Skip invalid question IDs
            if (!validQuestionIds.contains(questionId)) {
                continue;
            }

            ExamQuestion question = examQuestionRepository.findById(questionId).orElse(null);
            QuestionResult result = new QuestionResult();
            result.setQuestionId(questionId.toString());

            if (question == null) {
                result.setCorrect(false);
                result.setUserAnswer(-1);
                result.setCorrectAnswer(-1);
                skipped++;
            } else {
                int userAnswerIndex = convertOptionToIndex(userAnswer);
                int correctAnswerIndex = convertOptionToIndex(question.getCorrectOption());
                
                result.setCorrect(userAnswer != null && userAnswer.equals(question.getCorrectOption()));
                result.setUserAnswer(userAnswerIndex);
                result.setCorrectAnswer(correctAnswerIndex);
                result.setExplanation("Not yet provided");

                if (userAnswer == null || userAnswerIndex == -1) {
                    skipped++;
                } else if (result.isCorrect()) {
                    correctAnswers++;
                } else {
                    incorrectAnswers++;
                }
            }
            
            detailedResults.add(result);
            validQuestionIds.remove(questionId);
        }

        // Add unattempted questions to detailedResults
        for (Long unattemptedQuestionId : validQuestionIds) {
            ExamQuestion question = examQuestionRepository.findById(unattemptedQuestionId).orElse(null);
            QuestionResult result = new QuestionResult();
            result.setQuestionId(unattemptedQuestionId.toString());
            result.setCorrect(false);
            result.setUserAnswer(-1);
            result.setCorrectAnswer(question != null ? convertOptionToIndex(question.getCorrectOption()) : -1);
            result.setExplanation(question != null ? "Not Yet provided" : null);
            skipped++;
            detailedResults.add(result);
        }

        // Ensure skipped count aligns with totalQuestions
        skipped = totalQuestions - (correctAnswers + incorrectAnswers);

        double score = totalQuestions > 0 ? ((double) correctAnswers / totalQuestions) * 100 : 0.0;
        score = Math.round(score * 10.0) / 10.0; // Round to 1 decimal place
        boolean passed = score >= 80;

        return new ExamAttemptResponse(
            0, // ID will be set by the database
            request.getUserId(),
            courseName,
            score,
            passed,
            LocalDateTime.now().toString(),
            request.getTimeTaken(),
            correctAnswers,
            incorrectAnswers,
            skipped,
            detailedResults,
            totalQuestions
        );
    }

    private int convertOptionToIndex(String option) {
        if (option == null) return -1;
        switch (option.toUpperCase()) {
            case "A": return 0;
            case "B": return 1;
            case "C": return 2;
            case "D": return 3;
            default: return -1;
        }
    }
}
