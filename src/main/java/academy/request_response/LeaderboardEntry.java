package academy.request_response;

import java.time.LocalDateTime;

public class LeaderboardEntry {
    private Long userId;
    private String username;
    private double bestScore;
    private int totalAttempts;
    private LocalDateTime lastAttempt;
    private int rank;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public double getBestScore() {
		return bestScore;
	}
	public void setBestScore(double bestScore) {
		this.bestScore = bestScore;
	}
	public int getTotalAttempts() {
		return totalAttempts;
	}
	public void setTotalAttempts(int totalAttempts) {
		this.totalAttempts = totalAttempts;
	}
	public LocalDateTime getLastAttempt() {
		return lastAttempt;
	}
	public void setLastAttempt(LocalDateTime lastAttempt) {
		this.lastAttempt = lastAttempt;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public LeaderboardEntry(Long userId, String username, double bestScore, int totalAttempts,
			LocalDateTime lastAttempt, int rank) {
		super();
		this.userId = userId;
		this.username = username;
		this.bestScore = bestScore;
		this.totalAttempts = totalAttempts;
		this.lastAttempt = lastAttempt;
		this.rank = rank;
	}
	public LeaderboardEntry() {
		super();
		// TODO Auto-generated constructor stub
	}

    // Constructors, getters, setters...
}
