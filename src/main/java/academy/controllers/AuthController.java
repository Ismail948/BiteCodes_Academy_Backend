package academy.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import academy.models.User;
import academy.repositories.UserRepository;
import academy.request_response.LoginRequest;
import academy.request_response.LoginResponse;
import academy.request_response.OtpVerificationRequest;
import academy.request_response.UserUpdateDTO;
import academy.services.ApiResponse;
import academy.services.CustomUserDetailsService;
import academy.services.EmailService;
import academy.services.JwtService;
import academy.services.OtpService;
import academy.services.PremiumService;
import academy.services.RegistrationService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final EmailService emailService;
    private final OtpService otpService;
    private final PremiumService premiumService;

    @Autowired
    public AuthController(
            RegistrationService registrationService,
            UserRepository userRepository,
            JwtService jwtService,
            EmailService emailService,
            AuthenticationManager authenticationManager,
            OtpService otpService,
            CustomUserDetailsService userDetailsService,
            PremiumService premiumService) {
        this.registrationService = registrationService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
        this.otpService = otpService;
        this.premiumService = premiumService;
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UserUpdateDTO updatedUser) {
        try {
            Long parsedId;
            try {
                parsedId = Long.valueOf(id);
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(false, "Invalid user ID: " + id));
            }
            
            User existingUser = userRepository.findById(parsedId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + parsedId));

            // Update basic fields
            if (updatedUser.getUsername() != null) existingUser.setUsername(updatedUser.getUsername());
            if (updatedUser.getName() != null) existingUser.setName(updatedUser.getName());
            if (updatedUser.getProfileurl() != null) existingUser.setProfileurl(updatedUser.getProfileurl());
            if (updatedUser.getPhonenum() != null) existingUser.setPhonenum(updatedUser.getPhonenum());
            if (updatedUser.getState() != null) existingUser.setState(updatedUser.getState());
            if (updatedUser.getPassword() != null) existingUser.setPassword(updatedUser.getPassword());
            if (updatedUser.getBio() != null) existingUser.setBio(updatedUser.getBio());
            if (updatedUser.getRole() != null) existingUser.setRole(updatedUser.getRole());
            if (updatedUser.getTimezone() != null) existingUser.setTimezone(updatedUser.getTimezone());
            if (updatedUser.getAvailability() != null) existingUser.setAvailability(updatedUser.getAvailability());

            userRepository.save(existingUser);
            return ResponseEntity.ok(new ApiResponse(true, "User updated successfully."));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Update failed: " + e.getMessage()));
        }
    }

    @PostMapping("/google-auth")
    public ResponseEntity<Map<String, Object>> googleAuth(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String name = request.get("name");
            String picture = request.get("picture");
            String password = "Google";

            if (email == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Google OAuth failed: Email is missing.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            Optional<User> existingUser = userRepository.findByEmail(email);
            User user;

            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                user = new User();
                user.setEmail(email);
                user.setName(name != null ? name : "Google User");
                user.setProfileurl(picture != null ? picture : "https://webcrumbs.cloud/placeholder");
                user.setUsername(email.split("@")[0]);
                user.setPassword(password);
                user.setRole("USER");
                user.setEnabled(true);
                userRepository.save(user);
            }

            // Get premium status for the user
            Map<String, Object> premiumStatus = premiumService.getPremiumStatus(user.getId());
            
            String jwtToken = jwtService.generateToken(user.getEmail(), user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Google login successful");
            response.put("token", jwtToken);
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("profileurl", user.getProfileurl());
            response.put("userid", user.getId());
            response.put("name", user.getName());
            response.put("role", user.getRole());
            response.put("premiumStatus", premiumStatus);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Google OAuth failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            if (!registrationService.validUser(user)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Registration failed: Email or Username Already Exist");
            }
            registrationService.registerUser(user);
            return ResponseEntity.ok("User registered. Check your email for OTP.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/resend-otp/{email}")
    public ResponseEntity<String> resendOtp(@PathVariable String email) {
        String otp = otpService.generateOtp();
        LocalDateTime otpExpiry = otpService.getOtpExpiryTime();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        user.setOtp(otp);
        user.setOtpExpiry(otpExpiry);
        userRepository.save(user);
        new Thread(() -> emailService.sendOtpEmail(email, otp)).start();
        return ResponseEntity.status(HttpStatus.OK).body("OTP Sent Successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody OtpVerificationRequest request) {
        boolean verified = registrationService.verifyOtp(request.getEmail(), request.getOtp());
        if (verified) {
            return ResponseEntity.ok(new ApiResponse(true, "Email verified!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Invalid or expired OTP."));
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String id) {
        try {
            Long parsedId;
            try {
                parsedId = Long.valueOf(id);
            } catch (NumberFormatException e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid user ID: " + id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            User user = userRepository.findById(parsedId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with Id: " + parsedId));
            
            // Get premium status and include it in response
            Map<String, Object> premiumStatus = premiumService.getPremiumStatus(parsedId);
            
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("id", user.getId());
            userResponse.put("username", user.getUsername() != null ? user.getUsername() : "");
            userResponse.put("name", user.getName() != null ? user.getName() : "");
            userResponse.put("email", user.getEmail());
            userResponse.put("profileurl", user.getProfileurl() != null ? user.getProfileurl() : "");
            userResponse.put("phonenum", user.getPhonenum() != null ? user.getPhonenum() : "");
            userResponse.put("state", user.getState() != null ? user.getState() : "");
            userResponse.put("bio", user.getBio() != null ? user.getBio() : "");
            userResponse.put("role", user.getRole() != null ? user.getRole() : "");
            userResponse.put("timezone", user.getTimezone() != null ? user.getTimezone() : "");
            userResponse.put("availability", user.getAvailability() != null ? user.getAvailability() : "");
            userResponse.put("enabled", user.isEnabled());
            userResponse.put("createdAt", user.getCreatedAt());
            userResponse.put("premiumStatus", premiumStatus);
            
            return ResponseEntity.ok(userResponse);
        } catch (UsernameNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/getUsername/{id}")
    public ResponseEntity<?> getUsernameById(@PathVariable String id) {
        try {
            Long parsedId;
            try {
                parsedId = Long.valueOf(id);
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(false, "Invalid user ID: " + id));
            }
            
            User user = userRepository.findById(parsedId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with Id: " + parsedId));
            
            return ResponseEntity.ok(user.getUsername());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to fetch username: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            if (password.equals(user.getPassword())) {
                String token = jwtService.generateToken(user.getEmail(), user.getId());
                String username = user.getUsername();
                String phonenum = (user.getPhonenum() != null) ? user.getPhonenum() : "";
                String state = (user.getState() != null) ? user.getState() : "";
                Long userid = (user.getId() != null) ? user.getId() : 0;
                String profileurl = (user.getProfileurl() != null) ? user.getProfileurl() : "";
                String name = (user.getName() != null) ? user.getName() : "";
                
                // Get premium status
                Map<String, Object> premiumStatus = premiumService.getPremiumStatus(user.getId());
                
                // Enhanced login response with premium status
                Map<String, Object> loginResponse = new HashMap<>();
                loginResponse.put("success", true);
                loginResponse.put("token", token);
                loginResponse.put("username", username);
                loginResponse.put("email", email);
                loginResponse.put("phonenum", phonenum);
                loginResponse.put("state", state);
                loginResponse.put("userid", userid);
                loginResponse.put("profileurl", profileurl);
                loginResponse.put("name", name);
                loginResponse.put("role", user.getRole() != null ? user.getRole() : "USER");
                loginResponse.put("premiumStatus", premiumStatus);
                
                return ResponseEntity.ok(loginResponse);
            }

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid email or password.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            
        } catch (UsernameNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            
            // Enhance user data with premium status for admin views
            List<Map<String, Object>> enhancedUsers = users.stream().map(user -> {
                Map<String, Object> premiumStatus = premiumService.getPremiumStatus(user.getId());
                
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("username", user.getUsername() != null ? user.getUsername() : "");
                userMap.put("name", user.getName() != null ? user.getName() : "");
                userMap.put("email", user.getEmail());
                userMap.put("profileurl", user.getProfileurl() != null ? user.getProfileurl() : "");
                userMap.put("phonenum", user.getPhonenum() != null ? user.getPhonenum() : "");
                userMap.put("state", user.getState() != null ? user.getState() : "");
                userMap.put("role", user.getRole() != null ? user.getRole() : "");
                userMap.put("enabled", user.isEnabled());
                userMap.put("createdAt", user.getCreatedAt());
                userMap.put("premiumStatus", premiumStatus);
                
                return userMap;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(enhancedUsers);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch users: " + e.getMessage());
            
            // Return error in same format but as single item list for consistency
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of(errorResponse));
        }
    }
}