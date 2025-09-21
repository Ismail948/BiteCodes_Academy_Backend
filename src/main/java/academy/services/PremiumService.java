// academy/services/PremiumService.java
package academy.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import academy.models.Coupon;
import academy.models.CouponUsage;
import academy.models.Premium;
import academy.models.Purchase;
import academy.models.PurchaseStatus;
import academy.models.SubscriptionType;
import academy.models.User;
import academy.repositories.CouponRepository;
import academy.repositories.CouponUsageRepository;
import academy.repositories.PremiumRepository;
import academy.repositories.PurchaseRepo;
import academy.repositories.UserRepository;

@Service
public class PremiumService {

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final PurchaseRepo purchaseRepository;
    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final PaymentService paymentService;
    private final CouponService couponService;

    private final String razorpayKeyId = System.getenv("RAZORPAY_KEY_ID") != null ? 
        System.getenv("RAZORPAY_KEY_ID") : "rzp_test_RDc7AGHUscKt8H";
    private final String razorpaySecret = System.getenv("RAZORPAY_SECRET") != null ? 
        System.getenv("RAZORPAY_SECRET") : "fDIjCY5SYvPmNrNBX7FDFZmV";

    private static final Map<SubscriptionType, Double> PREMIUM_PRICES = Map.of(
            SubscriptionType.MONTHLY, 99.0,
            SubscriptionType.QUARTERLY, 249.0,
            SubscriptionType.HALF_YEARLY, 449.0,
            SubscriptionType.YEARLY, 799.0,
            SubscriptionType.LIFETIME, 2999.0
    );

    public PremiumService(UserRepository userRepository,
                          PremiumRepository premiumRepository,
                          PurchaseRepo purchaseRepository,
                          CouponRepository couponRepository,
                          CouponUsageRepository couponUsageRepository,
                          PaymentService paymentService,
                          CouponService couponService) {
        this.userRepository = userRepository;
        this.premiumRepository = premiumRepository;
        this.purchaseRepository = purchaseRepository;
        this.couponRepository = couponRepository;
        this.couponUsageRepository = couponUsageRepository;
        this.paymentService = paymentService;
        this.couponService = couponService;
    }

    @Cacheable(value = "premiumStatusCache", key = "#userId")
    public Map<String, Object> getPremiumStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> status = new HashMap<>();
        Premium premium = user.getPremium();

        if (premium != null && premium.isActive()) {
            status.put("hasPremium", true);
            status.put("subscriptionType", premium.getSubscriptionType().toString());
            status.put("startDate", premium.getStartDate().toString());
            status.put("endDate", premium.getEndDate().toString());
            status.put("daysRemaining", 
                java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), premium.getEndDate()));
        } else {
            status.put("hasPremium", false);
            status.put("subscriptionType", null);
            status.put("startDate", null);
            status.put("endDate", null);
            status.put("daysRemaining", 0);
        }

        return status;
    }

    public Map<String, Object> getPremiumPricing() {
        Map<String, Object> pricing = new HashMap<>();
        
        for (Map.Entry<SubscriptionType, Double> entry : PREMIUM_PRICES.entrySet()) {
            Map<String, Object> planDetails = new HashMap<>();
            planDetails.put("price", entry.getValue());
            planDetails.put("duration", getDurationText(entry.getKey()));
            planDetails.put("savings", calculateSavings(entry.getKey()));
            planDetails.put("popular", entry.getKey() == SubscriptionType.YEARLY);
            pricing.put(entry.getKey().toString(), planDetails);
        }
        
        return pricing;
    }

    private String getDurationText(SubscriptionType type) {
        return switch (type) {
            case MONTHLY -> "1 Month";
            case QUARTERLY -> "3 Months";
            case HALF_YEARLY -> "6 Months";
            case YEARLY -> "1 Year";
            case LIFETIME -> "Lifetime";
        };
    }

    private String calculateSavings(SubscriptionType type) {
        double monthlyPrice = PREMIUM_PRICES.get(SubscriptionType.MONTHLY);
        double planPrice = PREMIUM_PRICES.get(type);
        
        return switch (type) {
            case MONTHLY -> "No savings";
            case QUARTERLY -> String.format("Save ₹%.0f", (monthlyPrice * 3) - planPrice);
            case HALF_YEARLY -> String.format("Save ₹%.0f", (monthlyPrice * 6) - planPrice);
            case YEARLY -> String.format("Save ₹%.0f", (monthlyPrice * 12) - planPrice);
            case LIFETIME -> "Best Value - Pay Once!";
        };
    }

    @Transactional
    public Map<String, Object> createPremiumOrder(Long userId, SubscriptionType subscriptionType,
                                                 String paymentMethod, String upiId, String appName,
                                                 String couponCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Double originalAmount = PREMIUM_PRICES.get(subscriptionType);
        if (originalAmount == null) {
            throw new RuntimeException("Invalid subscription type");
        }

        Double discountAmount = 0.0;
        Coupon coupon = null;

        if (couponCode != null && !couponCode.trim().isEmpty()) {
            coupon = couponService.validateAndApplyCoupon(couponCode, userId, originalAmount);
            discountAmount = coupon.calculateDiscount(originalAmount);
        }

        Double finalAmount = originalAmount - discountAmount;

        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpaySecret);
            int amountInPaise = (int) (finalAmount * 100);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("payment_capture", 1);
            orderRequest.put("receipt", "premium_" + userId + "_" + System.currentTimeMillis());

            Order order = razorpay.orders.create(orderRequest);
            String orderId = order.get("id");

            // Create purchase record (paymentId is null at this stage)
            Purchase purchase = new Purchase(user, orderId, originalAmount, subscriptionType, paymentMethod, null);
            if (coupon != null) {
                purchase.applyCoupon(couponCode, discountAmount);
            }
            purchaseRepository.save(purchase);

            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", orderId);
            response.put("amount", amountInPaise);
            response.put("currency", "INR");
            response.put("originalAmount", originalAmount);
            response.put("discountAmount", discountAmount);
            response.put("finalAmount", finalAmount);
            response.put("subscriptionType", subscriptionType.toString());
            
            if (coupon != null) {
                response.put("couponApplied", true);
                response.put("couponCode", couponCode);
                response.put("couponName", coupon.getName());
            }

            return response;
        } catch (Exception e) {
            System.err.println("Error creating Razorpay order: " + e.getMessage());
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage(), e);
        }
    }

    @Transactional
    @CacheEvict(value = "premiumStatusCache", key = "#userId")
    public boolean verifyPaymentAndActivatePremium(Long userId, String paymentId, String orderId, String signature) {
        try {
            System.out.println("Verifying payment for userId: " + userId + ", orderId: " + orderId + ", paymentId: " + paymentId);

            String data = orderId + "|" + paymentId;
            String generatedSignature = HmacUtils.hmacSha256Hex(razorpaySecret, data);
            
            if (!generatedSignature.equals(signature)) {
                System.err.println("Signature verification failed. Expected: " + generatedSignature + ", Received: " + signature);
                return false;
            }

            Purchase purchase = purchaseRepository.findByTransactionId(orderId);
            if (purchase == null) {
                System.err.println("Purchase record not found for orderId: " + orderId);
                throw new RuntimeException("Purchase record not found");
            }

            User user = purchase.getUser();
            if (!user.getId().equals(userId)) {
                System.err.println("User ID mismatch. Expected: " + userId + ", Found: " + user.getId());
                throw new RuntimeException("User ID mismatch");
            }

            purchase.setPurchaseStatus(PurchaseStatus.COMPLETED);
            purchase.setPaymentId(paymentId);
            purchase.setUpdatedAt(LocalDateTime.now());
            purchaseRepository.save(purchase);

            Premium existingPremium = user.getPremium();
            if (existingPremium != null) {
                existingPremium.setIsActive(false);
                existingPremium.setUpdatedAt(LocalDateTime.now());
                premiumRepository.save(existingPremium);
            }

            Premium newPremium = new Premium(user, purchase.getSubscriptionType(), 
                                           purchase.getFinalAmount(), purchase.getTransactionId());
            
            if (purchase.getCouponCode() != null) {
                newPremium.setCouponUsed(purchase.getCouponCode());
                newPremium.setDiscountApplied(purchase.getDiscountAmount());
                
                Coupon coupon = couponRepository.findByCode(purchase.getCouponCode())
                        .orElseThrow(() -> new RuntimeException("Coupon not found"));
                
                coupon.incrementUsedQuantity();
                couponRepository.save(coupon);
                
                CouponUsage usage = new CouponUsage(user, coupon, purchase.getOriginalAmount(),
                                                  purchase.getDiscountAmount(), purchase.getTransactionId());
                couponUsageRepository.save(usage);
            }

            premiumRepository.save(newPremium);
            user.setPremium(newPremium);
            userRepository.save(user);

            System.out.println("Payment verified and premium activated for userId: " + userId);
            return true;
        } catch (Exception e) {
            System.err.println("Error verifying payment for userId: " + userId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error verifying payment: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getPremiumHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Purchase> purchases = purchaseRepository.findByUserOrderByPurchaseDateDesc(user);
        List<Premium> premiumHistory = premiumRepository.findByUserOrderByCreatedAtDesc(user);

        Map<String, Object> history = new HashMap<>();
        history.put("purchases", purchases);
        history.put("premiumSubscriptions", premiumHistory);

        return history;
    }

    @Transactional
    @CacheEvict(value = "premiumStatusCache", key = "#userId")
    public boolean cancelPremiumSubscription(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Premium premium = user.getPremium();
        if (premium != null && premium.isActive()) {
            premium.setIsActive(false);
            premium.setUpdatedAt(LocalDateTime.now());
            premiumRepository.save(premium);
            
            user.setPremium(null);
            userRepository.save(user);
            
            return true;
        }

        return false;
    }

    public boolean userHasPremium(Long userId) {
        Map<String, Object> status = getPremiumStatus(userId);
        return (Boolean) status.get("hasPremium");
    }
}