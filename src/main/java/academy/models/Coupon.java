package academy.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "coupons")
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "code", unique = true, nullable = false)
    private String code;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_type", nullable = false)
    private CouponType couponType;
    
    @Column(name = "discount_percentage", nullable = false)
    private Integer discountPercentage; // 0-100
    
    @Column(name = "max_discount_amount")
    private Double maxDiscountAmount;
    
    @Column(name = "min_purchase_amount")
    private Double minPurchaseAmount;
    
    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;
    
    @Column(name = "used_quantity", nullable = false)
    private Integer usedQuantity = 0;
    
    @Column(name = "holder_name")
    private String holderName; // Person who created/owns this coupon
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "usage_limit_per_user")
    private Integer usageLimitPerUser = 1;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public Coupon() {}
    
    public Coupon(String code, String name, CouponType couponType, Integer discountPercentage, 
                  Integer totalQuantity, String holderName, LocalDateTime startDate, LocalDateTime endDate) {
        this.code = code;
        this.name = name;
        this.couponType = couponType;
        this.discountPercentage = discountPercentage;
        this.totalQuantity = totalQuantity;
        this.holderName = holderName;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Helper methods
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && 
               now.isAfter(startDate) && 
               now.isBefore(endDate) && 
               usedQuantity < totalQuantity;
    }
    
    public boolean canBeUsed() {
        return isValid() && hasAvailableQuantity();
    }
    
    public boolean hasAvailableQuantity() {
        return usedQuantity < totalQuantity;
    }
    
    public void incrementUsedQuantity() {
        if (hasAvailableQuantity()) {
            this.usedQuantity++;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new RuntimeException("Coupon has reached maximum usage limit");
        }
    }
    
    public Double calculateDiscount(Double originalAmount) {
        if (!canBeUsed() || originalAmount < minPurchaseAmount) {
            return 0.0;
        }
        
        double discountAmount = (originalAmount * discountPercentage) / 100.0;
        
        // Apply max discount limit if set
        if (maxDiscountAmount != null && discountAmount > maxDiscountAmount) {
            discountAmount = maxDiscountAmount;
        }
        
        return discountAmount;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public CouponType getCouponType() {
        return couponType;
    }
    
    public void setCouponType(CouponType couponType) {
        this.couponType = couponType;
    }
    
    public Integer getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    
    public Double getMaxDiscountAmount() {
        return maxDiscountAmount;
    }
    
    public void setMaxDiscountAmount(Double maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }
    
    public Double getMinPurchaseAmount() {
        return minPurchaseAmount;
    }
    
    public void setMinPurchaseAmount(Double minPurchaseAmount) {
        this.minPurchaseAmount = minPurchaseAmount;
    }
    
    public Integer getTotalQuantity() {
        return totalQuantity;
    }
    
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    
    public Integer getUsedQuantity() {
        return usedQuantity;
    }
    
    public void setUsedQuantity(Integer usedQuantity) {
        this.usedQuantity = usedQuantity;
    }
    
    public String getHolderName() {
        return holderName;
    }
    
    public void setHolderName(String holderName) {
        this.holderName = holderName;
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getUsageLimitPerUser() {
        return usageLimitPerUser;
    }
    
    public void setUsageLimitPerUser(Integer usageLimitPerUser) {
        this.usageLimitPerUser = usageLimitPerUser;
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
