package academy.services;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import academy.models.Coupon;
import academy.models.CouponUsage;
import academy.models.SubscriptionType;
import academy.models.User;
import academy.repositories.CouponRepository;
import academy.repositories.CouponUsageRepository;
import academy.repositories.UserRepository;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final UserRepository userRepository;

    public CouponService(CouponRepository couponRepository,
                         CouponUsageRepository couponUsageRepository,
                         UserRepository userRepository) {
        this.couponRepository = couponRepository;
        this.couponUsageRepository = couponUsageRepository;
        this.userRepository = userRepository;
    }

    // Validate coupon and return details
    public Map<String, Object> validateCoupon(String couponCode, Long userId, SubscriptionType subscriptionType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Coupon coupon = couponRepository.findByCode(couponCode.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

        Map<String, Object> result = new HashMap<>();

        // Check if coupon is valid
        if (!coupon.canBeUsed()) {
            if (!coupon.getIsActive()) {
                throw new RuntimeException("Coupon is inactive");
            } else if (LocalDateTime.now().isBefore(coupon.getStartDate())) {
                throw new RuntimeException("Coupon is not yet active");
            } else if (LocalDateTime.now().isAfter(coupon.getEndDate())) {
                throw new RuntimeException("Coupon has expired");
            } else if (!coupon.hasAvailableQuantity()) {
                throw new RuntimeException("Coupon usage limit reached");
            }
        }

        // Check user-specific usage limit
        long userUsageCount = couponUsageRepository.countByUserAndCoupon(user, coupon);
        if (userUsageCount >= coupon.getUsageLimitPerUser()) {
            throw new RuntimeException("You have already used this coupon the maximum number of times");
        }

        // Get premium price for calculation
        Double originalAmount = getPremiumPrice(subscriptionType);
        
        // Check minimum purchase amount
        if (coupon.getMinPurchaseAmount() != null && originalAmount < coupon.getMinPurchaseAmount()) {
            throw new RuntimeException("Minimum purchase amount of ₹" + coupon.getMinPurchaseAmount() + " required");
        }

        // Calculate discount
        Double discountAmount = coupon.calculateDiscount(originalAmount);

        result.put("valid", true);
        result.put("couponId", coupon.getId());
        result.put("couponCode", coupon.getCode());
        result.put("couponName", coupon.getName());
        result.put("description", coupon.getDescription());
        result.put("discountPercentage", coupon.getDiscountPercentage());
        result.put("originalAmount", originalAmount);
        result.put("discountAmount", discountAmount);
        result.put("finalAmount", originalAmount - discountAmount);
        result.put("maxDiscountAmount", coupon.getMaxDiscountAmount());
        result.put("minPurchaseAmount", coupon.getMinPurchaseAmount());
        result.put("expiryDate", coupon.getEndDate());
        result.put("remainingUsage", coupon.getTotalQuantity() - coupon.getUsedQuantity());

        return result;
    }

    // Validate and apply coupon (used during purchase)
    @Transactional
    public Coupon validateAndApplyCoupon(String couponCode, Long userId, Double originalAmount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Coupon coupon = couponRepository.findByCode(couponCode.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

        // Validate coupon
        if (!coupon.canBeUsed()) {
            throw new RuntimeException("Coupon cannot be used");
        }

        // Check user usage limit
        long userUsageCount = couponUsageRepository.countByUserAndCoupon(user, coupon);
        if (userUsageCount >= coupon.getUsageLimitPerUser()) {
            throw new RuntimeException("Coupon usage limit exceeded for user");
        }

        // Check minimum purchase amount
        if (coupon.getMinPurchaseAmount() != null && originalAmount < coupon.getMinPurchaseAmount()) {
            throw new RuntimeException("Minimum purchase amount not met");
        }

        return coupon;
    }

    // Get all active coupons
    public List<Coupon> getActiveCoupons() {
        return couponRepository.findByIsActiveTrueAndEndDateAfterOrderByCreatedAtDesc(LocalDateTime.now());
    }

    // Get coupons by type
    public List<Coupon> getCouponsByType(String couponType) {
        return couponRepository.findByCouponTypeAndIsActiveTrueAndEndDateAfterOrderByCreatedAtDesc(
                academy.models.CouponType.valueOf(couponType.toUpperCase()),
                LocalDateTime.now()
        );
    }

    // Create new coupon
    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        // Ensure code is uppercase
        coupon.setCode(coupon.getCode().toUpperCase());
        
        // Check if code already exists
        if (couponRepository.findByCode(coupon.getCode()).isPresent()) {
            throw new RuntimeException("Coupon code already exists");
        }

        return couponRepository.save(coupon);
    }

    // Update coupon
    @Transactional
    public Coupon updateCoupon(Long couponId, Coupon updatedCoupon) {
        Coupon existingCoupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        // Update fields
        existingCoupon.setName(updatedCoupon.getName());
        existingCoupon.setDescription(updatedCoupon.getDescription());
        existingCoupon.setDiscountPercentage(updatedCoupon.getDiscountPercentage());
        existingCoupon.setMaxDiscountAmount(updatedCoupon.getMaxDiscountAmount());
        existingCoupon.setMinPurchaseAmount(updatedCoupon.getMinPurchaseAmount());
        existingCoupon.setTotalQuantity(updatedCoupon.getTotalQuantity());
        existingCoupon.setStartDate(updatedCoupon.getStartDate());
        existingCoupon.setEndDate(updatedCoupon.getEndDate());
        existingCoupon.setIsActive(updatedCoupon.getIsActive());
        existingCoupon.setUsageLimitPerUser(updatedCoupon.getUsageLimitPerUser());
        existingCoupon.setUpdatedAt(LocalDateTime.now());

        return couponRepository.save(existingCoupon);
    }

    // Deactivate coupon
    @Transactional
    public void deactivateCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        
        coupon.setIsActive(false);
        coupon.setUpdatedAt(LocalDateTime.now());
        couponRepository.save(coupon);
    }

    // Get coupon usage statistics
    public Map<String, Object> getCouponStats(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        List<CouponUsage> usages = couponUsageRepository.findByCouponOrderByUsedAtDesc(coupon);

        Map<String, Object> stats = new HashMap<>();
        stats.put("couponDetails", coupon);
        stats.put("totalUsages", coupon.getUsedQuantity());
        stats.put("remainingUsages", coupon.getTotalQuantity() - coupon.getUsedQuantity());
        stats.put("usagePercentage", (double) coupon.getUsedQuantity() / coupon.getTotalQuantity() * 100);
        stats.put("recentUsages", usages);
        
        // Calculate total discount given
        double totalDiscountGiven = usages.stream()
                .mapToDouble(CouponUsage::getDiscountAmount)
                .sum();
        stats.put("totalDiscountGiven", totalDiscountGiven);

        return stats;
    }

    // Get user's coupon usage history
    public List<CouponUsage> getUserCouponHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return couponUsageRepository.findByUserOrderByUsedAtDesc(user);
    }

    // Helper method to get premium price
    private Double getPremiumPrice(SubscriptionType subscriptionType) {
        return switch (subscriptionType) {
            case MONTHLY -> 99.0;
            case QUARTERLY -> 249.0;
            case HALF_YEARLY -> 449.0;
            case YEARLY -> 799.0;
            case LIFETIME -> 2999.0;
        };
    }
}