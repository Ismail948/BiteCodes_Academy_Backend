package academy.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "coupon_usage")
public class CouponUsage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;
    
    @Column(name = "original_amount", nullable = false)
    private Double originalAmount;
    
    @Column(name = "discount_amount", nullable = false)
    private Double discountAmount;
    
    @Column(name = "final_amount", nullable = false)
    private Double finalAmount;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt = LocalDateTime.now();
    
    // Constructors
    public CouponUsage() {}
    
    public CouponUsage(User user, Coupon coupon, Double originalAmount, 
                       Double discountAmount, String transactionId) {
        this.user = user;
        this.coupon = coupon;
        this.originalAmount = originalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = originalAmount - discountAmount;
        this.transactionId = transactionId;
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
    
    public Coupon getCoupon() {
        return coupon;
    }
    
    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }
    
    public Double getOriginalAmount() {
        return originalAmount;
    }
    
    public void setOriginalAmount(Double originalAmount) {
        this.originalAmount = originalAmount;
    }
    
    public Double getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public Double getFinalAmount() {
        return finalAmount;
    }
    
    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public LocalDateTime getUsedAt() {
        return usedAt;
    }
    
    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }
}