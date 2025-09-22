package academy.request_response;

import java.time.LocalDateTime;

public class TrendPoint {
    private LocalDateTime date;
    private double value;
    private String label;

    public TrendPoint(LocalDateTime date, double value, String label) {
        this.date = date;
        this.value = value;
        this.label = label;
    }

    // Getters and setters...
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}