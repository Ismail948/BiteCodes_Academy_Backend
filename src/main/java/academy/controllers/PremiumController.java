// academy/controllers/PremiumController.java
package academy.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import academy.models.SubscriptionType;
import academy.request_response.PremiumPurchaseRequest;
import academy.services.CouponService;
import academy.services.PremiumService;

@RestController
@RequestMapping("/api/premium")
@CrossOrigin(origins = "http://localhost:3000") // Restrict to frontend origin
public class PremiumController {

    private final PremiumService premiumService;
    private final CouponService couponService;

    public PremiumController(PremiumService premiumService, CouponService couponService) {
        this.premiumService = premiumService;
        this.couponService = couponService;
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<Map<String, Object>> getPremiumStatus(@PathVariable Long userId) {
        try {
            Map<String, Object> status = premiumService.getPremiumStatus(userId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to get premium status: " + e.getMessage()));
        }
    }

    @GetMapping("/pricing")
    public ResponseEntity<Map<String, Object>> getPremiumPricing() {
        Map<String, Object> pricing = premiumService.getPremiumPricing();
        return ResponseEntity.ok(pricing);
    }

    @PostMapping("/validate-coupon")
    public ResponseEntity<Map<String, Object>> validateCoupon(
            @RequestParam String couponCode,
            @RequestParam Long userId,
            @RequestParam SubscriptionType subscriptionType) {
        try {
            Map<String, Object> couponDetails = couponService.validateCoupon(couponCode, userId, subscriptionType);
            return ResponseEntity.ok(couponDetails);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage(), "valid", false));
        }
    }

    @PostMapping("/purchase/{userId}")
    public ResponseEntity<Map<String, Object>> purchasePremium(
            @PathVariable Long userId,
            @RequestBody PremiumPurchaseRequest request) {
        try {
            Map<String, Object> orderDetails = premiumService.createPremiumOrder(
                    userId,
                    request.getSubscriptionType(),
                    request.getPaymentMethod(),
                    request.getUpiId(),
                    request.getAppName(),
                    request.getCouponCode()
            );
            return ResponseEntity.ok(orderDetails);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to create premium order: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody Map<String, String> payload) {
        try {
            // Log payload for debugging
            System.out.println("Received verify-payment payload: " + payload);

            // Validate payload fields
            if (!payload.containsKey("razorpay_payment_id") || payload.get("razorpay_payment_id") == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Missing or null razorpay_payment_id"
                ));
            }
            if (!payload.containsKey("razorpay_order_id") || payload.get("razorpay_order_id") == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Missing or null razorpay_order_id"
                ));
            }
            if (!payload.containsKey("razorpay_signature") || payload.get("razorpay_signature") == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Missing or null razorpay_signature"
                ));
            }
            if (!payload.containsKey("userId") || payload.get("userId") == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Missing or null userId"
                ));
            }

            String razorpayPaymentId = payload.get("razorpay_payment_id");
            String razorpayOrderId = payload.get("razorpay_order_id");
            String razorpaySignature = payload.get("razorpay_signature");
            Long userId;
            try {
                userId = Long.parseLong(payload.get("userId"));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Invalid userId format"
                ));
            }

            boolean isValid = premiumService.verifyPaymentAndActivatePremium(
                    userId, razorpayPaymentId, razorpayOrderId, razorpaySignature
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", isValid);

            if (isValid) {
                response.put("message", "✅ Payment verified successfully & Premium activated!");
                Map<String, Object> premiumStatus = premiumService.getPremiumStatus(userId);
                response.put("premiumStatus", premiumStatus);
            } else {
                response.put("message", "❌ Payment verification failed");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in verify-payment endpoint: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error verifying payment: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<Map<String, Object>> getPremiumHistory(@PathVariable Long userId) {
        try {
            Map<String, Object> history = premiumService.getPremiumHistory(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to get premium history: " + e.getMessage()));
        }
    }

    @PostMapping("/cancel/{userId}")
    public ResponseEntity<Map<String, Object>> cancelPremium(@PathVariable Long userId) {
        try {
            boolean cancelled = premiumService.cancelPremiumSubscription(userId);
            if (cancelled) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Premium subscription cancelled successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message","No active premium subscription found"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to cancel premium: " + e.getMessage()
                    ));
        }
    }
}