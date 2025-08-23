package academy.models;


import jakarta.persistence.*;

@Entity
public class PreviousPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String examSlug; // Link to EntranceExam via slug
    private Integer year;
    private String pdfUrl;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty; // EASY, MEDIUM, HARD

    private Integer attemptedBy;
    private Integer topScore;

    @Column(length = 1000)
    private String notes;

    // Constructors
    public PreviousPaper() {}

    public PreviousPaper(String examSlug, Integer year, String pdfUrl, Difficulty difficulty, Integer attemptedBy, Integer topScore, String notes) {
        this.examSlug = examSlug;
        this.year = year;
        this.pdfUrl = pdfUrl;
        this.difficulty = difficulty;
        this.attemptedBy = attemptedBy;
        this.topScore = topScore;
        this.notes = notes;
    }

    // Getters & Setters
    public String getId() { return id; }
    public String getExamSlug() { return examSlug; }
    public void setExamSlug(String examSlug) { this.examSlug = examSlug; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public Integer getAttemptedBy() { return attemptedBy; }
    public void setAttemptedBy(Integer attemptedBy) { this.attemptedBy = attemptedBy; }

    public Integer getTopScore() { return topScore; }
    public void setTopScore(Integer topScore) { this.topScore = topScore; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

 enum Difficulty {
    EASY,
    MEDIUM,
    HARD,
    MODERATE
}
