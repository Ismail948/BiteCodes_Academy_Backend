package academy.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "premium_subscriptions")
public class Premium {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
//    @OneToOne
//    @JoinColumn(name = "user_id", unique = true)
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    @JsonIgnore  // Add this annotation
    private User user;
//    private User user;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", nullable = false)
    private SubscriptionType subscriptionType;
    
    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "coupon_used")
    private String couponUsed; // Store coupon code if used
    
    @Column(name = "discount_applied")
    private Double discountApplied = 0.0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public Premium() {}
    
    public Premium(User user, SubscriptionType subscriptionType, Double amountPaid, String transactionId) {
        this.user = user;
        this.subscriptionType = subscriptionType;
        this.amountPaid = amountPaid;
        this.transactionId = transactionId;
        this.startDate = LocalDateTime.now();
        this.endDate = calculateEndDate(subscriptionType);
        this.isActive = true;
    }
    
    // Calculate end date based on subscription type
    private LocalDateTime calculateEndDate(SubscriptionType type) {
        LocalDateTime start = LocalDateTime.now();
        return switch (type) {
            case MONTHLY -> start.plusMonths(1);
            case QUARTERLY -> start.plusMonths(3);
            case HALF_YEARLY -> start.plusMonths(6);
            case YEARLY -> start.plusYears(1);
            case LIFETIME -> start.plusYears(100); // Effectively lifetime
        };
    }
    
    // Check if premium is currently active
    public boolean isActive() {
        return isActive && LocalDateTime.now().isBefore(endDate);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }
    
    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
    
    public Double getAmountPaid() {
        return amountPaid;
    }
    
    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getCouponUsed() {
        return couponUsed;
    }
    
    public void setCouponUsed(String couponUsed) {
        this.couponUsed = couponUsed;
    }
    
    public Double getDiscountApplied() {
        return discountApplied;
    }
    
    public void setDiscountApplied(Double discountApplied) {
        this.discountApplied = discountApplied;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
