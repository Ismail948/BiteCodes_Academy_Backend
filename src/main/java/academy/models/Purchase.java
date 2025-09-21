package academy.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "purchases")
public class Purchase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "transaction_id", unique = true, nullable = false)
    private String transactionId;
    
    @Column(name = "original_amount", nullable = false)
    private Double originalAmount;
    
    @Column(name = "discount_amount")
    private Double discountAmount = 0.0;
    
    @Column(name = "final_amount", nullable = false)
    private Double finalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", nullable = false)
    private SubscriptionType subscriptionType;
    
    @Column(name = "coupon_code")
    private String couponCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "purchase_status", nullable = false)
    private PurchaseStatus purchaseStatus = PurchaseStatus.PENDING;
    @Column(name = "payment_id")
    private String paymentId;
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public Purchase() {}
    
    public Purchase(User user, String transactionId, Double originalAmount, 
                    SubscriptionType subscriptionType, String paymentMethod,String paymentId) {
        this.user = user;
        this.paymentId=paymentId;
        this.transactionId = transactionId;
        this.originalAmount = originalAmount;
        this.finalAmount = originalAmount;
        this.subscriptionType = subscriptionType;
        this.paymentMethod = paymentMethod;
    }
    
    // Apply coupon discount
    public void applyCoupon(String couponCode, Double discountAmount) {
        this.couponCode = couponCode;
        this.discountAmount = discountAmount;
        this.finalAmount = this.originalAmount - discountAmount;
        this.updatedAt = LocalDateTime.now();
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
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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
    
    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }
    
    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
    
    public String getCouponCode() {
        return couponCode;
    }
    
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
    
    public PurchaseStatus getPurchaseStatus() {
        return purchaseStatus;
    }
    
    public void setPurchaseStatus(PurchaseStatus purchaseStatus) {
        this.purchaseStatus = purchaseStatus;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

