package academy.controllers;

import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import academy.models.Course;
import academy.models.User;
import academy.repositories.CourseRepo;
import academy.repositories.UserRepository;
import academy.request_response.PurchaseRequest;
import academy.services.PurchaseService;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;

    private final UserRepository userRepository;

    private final CourseRepo courseRepository;

    
    
    private final String secret = "fDIjCY5SYvPmNrNBX7FDFZmV"; // Razorpay Secret
    public PurchaseController(PurchaseService purchaseService,CourseRepo courseRepository,UserRepository userRepository) {
        this.purchaseService = purchaseService;
        this.userRepository=userRepository;
        this.courseRepository=courseRepository;
    }

    @PostMapping("/buyCourse/{userId}/{courseSlug}")
    public ResponseEntity<String> buyCourse(@PathVariable Long userId,
                                            @PathVariable String courseSlug,
                                            @RequestBody PurchaseRequest request) {
        String transactionId = purchaseService.buyCourse(userId, courseSlug,
                request.getPaymentMethod(), request.getUpiId(), request.getAppName());
        return ResponseEntity.ok(transactionId);
    }
    public boolean verifyAndAddCourses(Long userId,
            String universitySlug,
            String paymentId,
            String orderId,
            String signature) {
			// 1️⃣ Verify Razorpay Signature
			String data = orderId + "|" + paymentId;
			String generatedSignature = HmacUtils.hmacSha256Hex(secret, data);
			
			if (!generatedSignature.equals(signature)) {
			return false; // ❌ Invalid Payment
			}
			
			// 2️⃣ Fetch user
			User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));
			
			// 3️⃣ Fetch all courses of the given university
			List<Course> courses = courseRepository.findByUniversitySlug(universitySlug);
			
			// 4️⃣ Add courses to user’s purchasedCourses (avoid duplicates)
			for (Course course : courses) {
			if (!user.getPurchasedCourses().contains(course)) {
			user.getPurchasedCourses().add(course);
			}
			}
			
			// 5️⃣ Save user
			userRepository.save(user);
			
			return true; // ✅ Success
			}
    @GetMapping("/hasAllCourses/{userId}/{universitySlug}")
    public ResponseEntity<Boolean> hasAllCourses(
            @PathVariable Long userId,
            @PathVariable String universitySlug) {
        boolean hasAll = purchaseService.hasUserBoughtAllCoursesOfUniversity(userId, universitySlug);
        return ResponseEntity.ok(hasAll);
    }


    @PostMapping("/buyAllCourses/{userId}")
    public ResponseEntity<String> buyAllCourses(@PathVariable Long userId,
                                                @RequestBody PurchaseRequest request) {
        String transactionId = purchaseService.buyAllCourses(userId,
                request.getPaymentMethod(), request.getUpiId(), request.getAppName());
        return ResponseEntity.ok(transactionId);
    }

    @PostMapping("/buyAllCoursesUniversity/{userId}/{universitySlug}")
    public ResponseEntity<Map<String, Object>> buyAllCoursesOfUniversity(
            @PathVariable Long userId,
            @PathVariable String universitySlug,
            @RequestBody PurchaseRequest request) {

        Map<String, Object> order = purchaseService.buyAllCoursesOfUniversity(
                userId, universitySlug,
                request.getPaymentMethod(),
                request.getUpiId(),
                request.getAppName()
        );

        return ResponseEntity.ok(order); // ✅ Returns JSON, not just a string
    }

//
//    @PostMapping("/verifyPayment")
//    public ResponseEntity<String> verifyPayment(@RequestBody Map<String, String> payload) {
//        String razorpayPaymentId = payload.get("razorpay_payment_id");
//        String razorpayOrderId = payload.get("razorpay_order_id");
//        String razorpaySignature = payload.get("razorpay_signature");
//
//        try {
//            String secret = "fDIjCY5SYvPmNrNBX7FDFZmV"; // from Razorpay dashboard
//
//            String data = razorpayOrderId + "|" + razorpayPaymentId;
//            String generatedSignature = HmacUtils.hmacSha256Hex(secret, data);
//
//            if (generatedSignature.equals(razorpaySignature)) {
//                // ✅ Payment is verified, mark courses as purchased
//                return ResponseEntity.ok("Payment verified successfully!");
//            } else {
//                return ResponseEntity.badRequest().body("Payment verification failed");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error verifying payment: " + e.getMessage());
//        }
//    }
    @PostMapping("/verifyPayment")
    public ResponseEntity<String> verifyPayment(@RequestBody Map<String, String> payload) {
        try {
            boolean isValid = purchaseService.verifyAndAddCourses(
                    Long.parseLong(payload.get("userId")),
                    payload.get("universitySlug"),
                    payload.get("razorpay_payment_id"),
                    payload.get("razorpay_order_id"),
                    payload.get("razorpay_signature")
            );

            if (isValid) {
                return ResponseEntity.ok("✅ Payment verified successfully & courses added!");
            } else {
                return ResponseEntity.badRequest().body("❌ Payment verification failed");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error verifying payment: " + e.getMessage());
        }
    }

}
