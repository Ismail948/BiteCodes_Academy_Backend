package academy.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import academy.models.ExamAttempt;
import academy.models.ExamQuestion;
import academy.models.QuestionResult;
import academy.models.Syllabus;
import academy.models.SyllabusSection;
import academy.models.TopicPerformance;
import academy.repositories.ExamAttemptRepository;
import academy.repositories.ExamQuestionRepository;
import academy.repositories.SyllabusRepository;
import academy.request_response.ExamAttemptRequest;
import academy.request_response.ExamAttemptResponse;
import academy.request_response.SectionWiseQuestionResponse;

@Service
public class ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final SyllabusRepository syllabusRepository;
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public ExamQuestionService(ExamQuestionRepository examQuestionRepository, 
                              SyllabusRepository syllabusRepository, 
                              ExamAttemptRepository examAttemptRepository,
                              RestTemplate restTemplate) {
        this.examQuestionRepository = examQuestionRepository;
        this.syllabusRepository = syllabusRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.restTemplate = restTemplate;
    }

 // New method to get attempt by ID

    
    public ExamAttemptResponse evaluateExam(ExamAttemptRequest request) {
        // Validate input
        if (request.getCourseName() == null) {
            throw new IllegalArgumentException("Course name cannot be null");
        }
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        String courseName = request.getCourseName();
        Map<Long, String> answers = request.getAnswers() != null ? request.getAnswers() : Collections.emptyMap();
        List<Long> questionIds = request.getQuestionIds() != null ? request.getQuestionIds() : Collections.emptyList();

        if (questionIds.isEmpty()) {
            throw new IllegalArgumentException("No question IDs provided in the request");
        }

        int totalQuestions = questionIds.size();
        Set<Long> validQuestionIds = questionIds.stream().collect(Collectors.toSet());

        List<QuestionResult> detailedResults = new ArrayList<>();
        Map<String, TopicPerformance> topicStats = new HashMap<>();
        
        int correctAnswers = 0;
        int incorrectAnswers = 0;
        int skipped = 0;

        // Process submitted answers
        for (Map.Entry<Long, String> entry : answers.entrySet()) {
            Long questionId = entry.getKey();
            String userAnswer = entry.getValue();

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
                result.setTopic("Unknown");
                result.setDifficulty("Unknown");
                result.setExplanation("Question not found in database");
                skipped++;
            } else {
                int userAnswerIndex = convertOptionToIndex(userAnswer);
                int correctAnswerIndex = convertOptionToIndex(question.getCorrectOption());
                
                boolean isCorrect = userAnswer != null && userAnswer.equals(question.getCorrectOption());
                result.setCorrect(isCorrect);
                result.setUserAnswer(userAnswerIndex);
                result.setCorrectAnswer(correctAnswerIndex);
                result.setExplanation("Analysis: " + generateExplanation(question, isCorrect));
                result.setTopic(question.getTopic());
                result.setDifficulty(question.getDifficulty() != null ? question.getDifficulty() : "medium");

                // Update topic statistics
                updateTopicStats(topicStats, question.getTopic(), isCorrect);

                if (userAnswer == null || userAnswerIndex == -1) {
                    skipped++;
                } else if (isCorrect) {
                    correctAnswers++;
                } else {
                    incorrectAnswers++;
                }
            }
            
            detailedResults.add(result);
            validQuestionIds.remove(questionId);
        }

        // Add unattempted questions
        for (Long unattemptedQuestionId : validQuestionIds) {
            ExamQuestion question = examQuestionRepository.findById(unattemptedQuestionId).orElse(null);
            QuestionResult result = new QuestionResult();
            result.setQuestionId(unattemptedQuestionId.toString());
            result.setCorrect(false);
            result.setUserAnswer(-1);
            result.setCorrectAnswer(question != null ? convertOptionToIndex(question.getCorrectOption()) : -1);
            result.setExplanation(question != null ? "Question not attempted" : "Question not found in database");
            result.setTopic(question != null ? question.getTopic() : "Unknown");
            result.setDifficulty(question != null && question.getDifficulty() != null ? question.getDifficulty() : "Unknown");

            if (question != null) {
                updateTopicStats(topicStats, question.getTopic(), false);
            }
            
            skipped++;
            detailedResults.add(result);
        }

        // Calculate final metrics
        double score = totalQuestions > 0 ? ((double) correctAnswers / totalQuestions) * 100 : 0.0;
        score = Math.round(score * 10.0) / 10.0;
        double cutoffScore = 80.0;
        boolean passed = score >= cutoffScore;

        // Generate AI Analysis using Gemini API
        String aiAnalysis = generateAIAnalysis(topicStats, score, correctAnswers, totalQuestions);
        String improvementSuggestions = generateImprovementSuggestions(topicStats, detailedResults);
        List<String> weakTopics = identifyWeakTopics(topicStats);
        List<String> strongTopics = identifyStrongTopics(topicStats);

        // Calculate percentile
        double percentile = calculatePercentile(score, courseName);

        // Determine difficulty based on performance
        String difficulty = determineDifficulty(score, percentile);
        // Save the exam attempt with all analytics data
        ExamAttempt attempt = new ExamAttempt();
        attempt.setUserId(request.getUserId());
        attempt.setCourseName(courseName);
        attempt.setScore(score);
        attempt.setPassed(passed);
        attempt.setAttemptedAt(LocalDateTime.now().toString());
        attempt.setTimeTaken(request.getTimeTaken());
        attempt.setCorrectAnswers(correctAnswers);
        attempt.setIncorrectAnswers(incorrectAnswers);
        attempt.setSkipped(skipped);
        attempt.setTotalQuestions(totalQuestions);
        attempt.setCutoffScore(cutoffScore);
        attempt.setPercentile(percentile);
        attempt.setDifficulty(difficulty);
        attempt.setTopicPerformance(topicStats);
        attempt.setAiAnalysis(aiAnalysis);
        attempt.setImprovementSuggestions(improvementSuggestions);
        attempt.setWeakTopics(weakTopics);
        attempt.setStrongTopics(strongTopics);
        attempt.setDetailedResults(detailedResults);

        ExamAttempt savedAttempt = examAttemptRepository.save(attempt);

        return new ExamAttemptResponse(
            savedAttempt.getId().longValue(),
            request.getUserId().longValue(),
            courseName,
            score,
            passed,
            savedAttempt.getAttemptedAt(),
            request.getTimeTaken(),
            correctAnswers,
            incorrectAnswers,
            skipped,
            detailedResults,
            totalQuestions,
            topicStats,
            aiAnalysis,
            improvementSuggestions,
            weakTopics,
            strongTopics,
            percentile,
            cutoffScore
        );
    }

    private void updateTopicStats(Map<String, TopicPerformance> topicStats, String topic, boolean isCorrect) {
        TopicPerformance performance = topicStats.computeIfAbsent(topic, k -> new TopicPerformance(topic, 0, 0, 0));
        performance.setTotalQuestions(performance.getTotalQuestions() + 1);
        
        if (isCorrect) {
            performance.setCorrectAnswers(performance.getCorrectAnswers() + 1);
        } else {
            performance.setIncorrectAnswers(performance.getIncorrectAnswers() + 1);
        }
        
        performance.setAccuracy(((double) performance.getCorrectAnswers() / performance.getTotalQuestions()) * 100);
    }

    private String determineDifficulty(double score, double percentile) {
        if (score >= 90 && percentile >= 80) {
            return "easy";
        } else if (score >= 70 && percentile >= 50) {
            return "medium";
        } else {
            return "hard";
        }
    }

    private String generateAIAnalysis(Map<String, TopicPerformance> topicStats, double score, int correct, int total) {
        // Prepare prompt for Gemini API
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a detailed performance analysis for a student's exam attempt.\n");
        prompt.append(String.format("Overall Score: %.1f%% (%d/%d correct)\n", score, correct, total));
        prompt.append("Topic-wise Breakdown:\n");
        for (TopicPerformance topic : topicStats.values()) {
            prompt.append(String.format("- %s: %.1f%% accuracy (%d/%d)\n", 
                topic.getTopicName(), topic.getAccuracy(), 
                topic.getCorrectAnswers(), topic.getTotalQuestions()));
        }
        prompt.append("\nProvide a concise analysis (100-200 words) including:\n");
        prompt.append("- Overall performance assessment\n");
        prompt.append("- Strengths and weaknesses by topic\n");
        prompt.append("- Recommendations for improvement\n");

        // Call Gemini API
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + geminiApiKey;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
            Map.of("parts", List.of(
                Map.of("text", prompt.toString())
            ))
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                @SuppressWarnings("unchecked")
                List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
                return parts.get(0).get("text");
            }
        } catch (Exception e) {
            // Fallback to default analysis if API fails
            System.err.println("Gemini API error: " + e.getMessage());
        }

        // Fallback analysis
        StringBuilder analysis = new StringBuilder();
        analysis.append("Performance Analysis:\n");
        analysis.append(String.format("Overall Score: %.1f%% (%d/%d correct)\n", score, correct, total));
        if (score >= 90) {
            analysis.append("Excellent performance! Strong grasp of material.\n");
        } else if (score >= 75) {
            analysis.append("Good performance, with room for improvement.\n");
        } else if (score >= 60) {
            analysis.append("Average performance. Focus on weak topics.\n");
        } else {
            analysis.append("Needs significant improvement.\n");
        }
        analysis.append("\nTopic-wise Breakdown:\n");
        for (TopicPerformance topic : topicStats.values()) {
            analysis.append(String.format("• %s: %.1f%% accuracy (%d/%d)\n", 
                topic.getTopicName(), topic.getAccuracy(), 
                topic.getCorrectAnswers(), topic.getTotalQuestions()));
        }
        return analysis.toString();
    }

    private String generateImprovementSuggestions(Map<String, TopicPerformance> topicStats, List<QuestionResult> results) {
        StringBuilder suggestions = new StringBuilder();
        suggestions.append("Personalized Study Recommendations:\n");
        
        List<String> weakAreas = topicStats.values().stream()
            .filter(tp -> tp.getAccuracy() < 70)
            .map(TopicPerformance::getTopicName)
            .collect(Collectors.toList());
            
        if (!weakAreas.isEmpty()) {
            suggestions.append("Priority Topics to Focus On:\n");
            for (String topic : weakAreas) {
                suggestions.append(String.format("• %s - Practice more questions and review concepts\n", topic));
            }
        }
        
        Map<String, Long> incorrectByTopic = results.stream()
            .filter(r -> !r.isCorrect() && r.getUserAnswer() != -1)
            .collect(Collectors.groupingBy(QuestionResult::getTopic, Collectors.counting()));
            
        if (!incorrectByTopic.isEmpty()) {
            suggestions.append("\nSpecific Areas Needing Attention:\n");
            incorrectByTopic.forEach((topic, count) -> 
                suggestions.append(String.format("• Review %s concepts (%d questions missed)\n", topic, count))
            );
        }
        
        suggestions.append("\nGeneral Tips:\n");
        suggestions.append("• Take more practice tests\n");
        suggestions.append("• Review explanations for incorrect answers\n");
        suggestions.append("• Focus on understanding concepts\n");
        
        return suggestions.toString();
    }

    private List<String> identifyWeakTopics(Map<String, TopicPerformance> topicStats) {
        return topicStats.values().stream()
            .filter(tp -> tp.getAccuracy() < 60)
            .map(TopicPerformance::getTopicName)
            .collect(Collectors.toList());
    }

    private List<String> identifyStrongTopics(Map<String, TopicPerformance> topicStats) {
        return topicStats.values().stream()
            .filter(tp -> tp.getAccuracy() >= 80)
            .map(TopicPerformance::getTopicName)
            .collect(Collectors.toList());
    }

    private double calculatePercentile(double score, String courseName) {
        List<ExamAttempt> allAttempts = examAttemptRepository.findByCourseName(courseName);
        if (allAttempts.isEmpty()) return 50.0;
        
        long lowerScores = allAttempts.stream()
            .mapToDouble(ExamAttempt::getScore)
            .filter(s -> s < score)
            .count();
            
        return ((double) lowerScores / allAttempts.size()) * 100;
    }

    private String generateExplanation(ExamQuestion question, boolean isCorrect) {
        if (isCorrect) {
            return "Correct! Well done on this " + question.getTopic() + " question.";
        } else {
            return "Incorrect. Review " + question.getTopic() + " concepts and practice similar questions.";
        }
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

    public List<ExamQuestion> getQuestionsByCourse(String courseName) {
        return examQuestionRepository.findByCourseName(courseName);
    }

    public ExamQuestion addQuestion(ExamQuestion question) {
        return examQuestionRepository.save(question);
    }
    
    public void addQuestions(List<ExamQuestion> questions) {
        examQuestionRepository.saveAll(questions);
    }

    public List<ExamQuestion> getRandomQuestions(String courseName, int count) {
        List<ExamQuestion> questions = examQuestionRepository.findByCourseName(courseName);
        Collections.shuffle(questions);
        return questions.stream().limit(count).collect(Collectors.toList());
    }

    public List<SectionWiseQuestionResponse> getRandomQuestionsBySyllabus(String courseName) {
        Optional<Syllabus> optionalSyllabus = syllabusRepository.findByCourseName(courseName);
        if (!optionalSyllabus.isPresent()) {
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
}