package academy.controllers;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Autowired
    public AuthController(
            RegistrationService registrationService,
            UserRepository userRepository,
            JwtService jwtService,
            EmailService emailService,
            AuthenticationManager authenticationManager,
            OtpService otpService,
            CustomUserDetailsService userDetailsService) {
        this.registrationService = registrationService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
        this.otpService = otpService;
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
    public ResponseEntity<?> googleAuth(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String name = request.get("name");
            String picture = request.get("picture");
            String password = "Google";
            String username = name;

            if (email == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Google OAuth failed: Email is missing."));
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

            String jwtToken = jwtService.generateToken(user.getEmail(), user.getId());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Google login successful",
                    "token", jwtToken,
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "profileurl", user.getProfileurl(),
                    "userid", user.getId(),
                    "name", user.getName(),
                    "role",user.getRole()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Google OAuth failed: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            if (!registrationService.validUser(user)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: Email or Username Already Exist");
            }
            registrationService.registerUser(user);
            return ResponseEntity.ok("User registered. Check your email for OTP.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
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
    public ResponseEntity<?> getUser(@PathVariable String id) {
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
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to fetch user: " + e.getMessage()));
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
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
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
                LoginResponse response = new LoginResponse(true, token, username, email, phonenum, state, userid, profileurl, name);
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid email or password."));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Login failed: " + e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to fetch users: " + e.getMessage()));
        }
    }

    
    

}