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

import academy.models.Course;
import academy.models.Purchase;
import academy.models.University;
import academy.models.User;
import academy.repositories.CourseRepo;
import academy.repositories.PurchaseRepo;
import academy.repositories.UniversityRepository;
import academy.repositories.UserRepository;

@Service
public class PurchaseService {

    private final UserRepository userRepository;
    private final CourseRepo courseRepository;
    private final UniversityRepository universityRepository;
    private final PurchaseRepo purchaseRepository;
    private final PaymentService paymentService;

    public PurchaseService(UserRepository userRepository,
                           CourseRepo courseRepository,
                           UniversityRepository universityRepository,
                           PurchaseRepo purchaseRepository,
                           PaymentService paymentService) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.universityRepository = universityRepository;
        this.purchaseRepository = purchaseRepository;
        this.paymentService = paymentService;
    }
    private final String secret = "fDIjCY5SYvPmNrNBX7FDFZmV"; // Razorpay Secret
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
//    public boolean hasUserBoughtAllCoursesOfUniversity(Long userId, String universitySlug) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        University university = universityRepository.findById(universitySlug)
//                .orElseThrow(() -> new RuntimeException("University not found"));
//
//        List<Course> uniCourses = university.getCourses();
//        List<Course> purchasedCourses = user.getPurchasedCourses();
//
//        // ✅ Check if user has all university courses
//        return purchasedCourses.containsAll(uniCourses);
//    }
    @Cacheable(value = "hasAllCoursesCache", key = "#userId + '-' + #universitySlug")
    public boolean hasUserBoughtAllCoursesOfUniversity(Long userId, String universitySlug) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        University university = universityRepository.findById(universitySlug)
                .orElseThrow(() -> new RuntimeException("University not found"));

        List<Course> uniCourses = university.getCourses();
        List<Course> purchasedCourses = user.getPurchasedCourses();

        return purchasedCourses.containsAll(uniCourses);
    }

    // ✅ Clear cache if user purchases new courses
    @CacheEvict(value = "hasAllCoursesCache", key = "#userId + '-' + #universitySlug")
    public void clearHasAllCoursesCache(Long userId, String universitySlug) {
        // called after purchase is added
    }

    // ✅ Buy one course (creates payment order)
    @Transactional
    public String buyCourse(Long userId, String courseSlug, String paymentMethod, String upiId, String appName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(courseSlug)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Create Razorpay order
        String transactionId = paymentService.createPayment(userId, course.getPrice(), paymentMethod, upiId, appName);

        // Save pending purchase
        Purchase purchase = new Purchase();
        purchase.setUserId(userId);
        purchase.setAmount(course.getPrice());
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setCourses(List.of(course));
        purchase.setTransactionId(transactionId);
        purchaseRepository.save(purchase);

        return transactionId; // Client completes payment with this transactionId
    }

    // ✅ Buy all courses
    @Transactional
    public String buyAllCourses(Long userId, String paymentMethod, String upiId, String appName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Course> allCourses = courseRepository.findAll();

        double totalPrice = allCourses.stream().mapToDouble(Course::getPrice).sum();

        // Create Razorpay order
        String transactionId = paymentService.createPayment(userId, totalPrice, paymentMethod, upiId, appName);

        // Save pending purchase
        Purchase purchase = new Purchase();
        purchase.setUserId(userId);
        purchase.setAmount(totalPrice);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setCourses(allCourses);
        purchase.setTransactionId(transactionId);
        purchaseRepository.save(purchase);

        return transactionId;
    }
    public double calculateUniversityCoursesPrice(String universitySlug) {
        University university = universityRepository.findById(universitySlug)
                .orElseThrow(() -> new RuntimeException("University not found"));

        if (university.getAllCoursesPrice() == null) {
            throw new IllegalStateException("allCoursesPrice is not set for " + universitySlug);
        }

        return university.getAllCoursesPrice();
    }
//    public String buyAllCoursesOfUniversity(Long userId, String universitySlug,
//		            String paymentMethod, String upiId, String appName) {
//		try {
//			RazorpayClient razorpay = new RazorpayClient("rzp_test_mQf2cASnEwehms", "unqMoyk2PrStZ0RxViTIrOCa");		
//		int amount = (int)(calculateUniversityCoursesPrice(universitySlug) * 100); // in paise
//		
//		JSONObject orderRequest = new JSONObject();
//		orderRequest.put("amount", amount);
//		orderRequest.put("currency", "INR");
//		orderRequest.put("payment_capture", 1);
//		
//		Order order = razorpay.orders.create(orderRequest);
//		
//		// Save mapping of orderId ↔ userId/universitySlug in DB for verification later
//		
//		return order.get("id"); // return Razorpay orderId
//		} catch (Exception e) {
//		throw new RuntimeException("Razorpay order creation failed", e);
//		}
//		}

    public Map<String, Object> buyAllCoursesOfUniversity(Long userId, String universitySlug,
            String paymentMethod, String upiId, String appName) {
		try {
		RazorpayClient razorpay = new RazorpayClient("rzp_test_RDc7AGHUscKt8H", "fDIjCY5SYvPmNrNBX7FDFZmV");
		
		int amount = (int)(calculateUniversityCoursesPrice(universitySlug) * 100); // in paise
		
		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount", amount);
		orderRequest.put("currency", "INR");
		orderRequest.put("payment_capture", 1);
		
		Order order = razorpay.orders.create(orderRequest);
		
		// ✅ Save mapping of orderId ↔ userId/universitySlug in DB for later verification
		
		Map<String, Object> response = new HashMap<>();
		response.put("razorpayOrderId", order.get("id"));
		response.put("amount", amount);
		response.put("currency", "INR");
		
		return response;
		} catch (Exception e) {
		throw new RuntimeException("Razorpay order creation failed", e);
		}
		}

    // ✅ Confirm purchase after successful payment
    @Transactional
    public String confirmPurchase(String transactionId) {
        Purchase purchase = purchaseRepository.findByTransactionId(transactionId);

        User user = userRepository.findById(purchase.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Add purchased courses to user
        user.getPurchasedCourses().addAll(purchase.getCourses());
        userRepository.save(user);

        return "Purchase confirmed and courses added to user!";
    }
}
