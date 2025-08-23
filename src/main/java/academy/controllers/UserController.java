package academy.controllers;


import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import academy.models.User;
import academy.repositories.UserRepository;



@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
      
        this.userRepository = userRepository;
         }
    
    @GetMapping("/getallusers")
    private List<User> getAllUsers(){
    	
    	return userRepository.findAll();
    }
    @PutMapping("/updateuser/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);
        if (!existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse("User not found", null));
        }
        User user = existingUser.get();
        user.setName(updatedUser.getName());
        user.setRole(updatedUser.getRole());
        user.setEnabled(updatedUser.isEnabled());
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse("User updated successfully", user));
    }

    @DeleteMapping("/deleteuser/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse("User not found", null));
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse("User deleted successfully", null));
    }
    public class ApiResponse {
        private String message;
        private Object data;
        public ApiResponse() {
        }
        public ApiResponse(String message, Object data) {
            this.message = message;
            this.data = data;
        }
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public Object getData() {
			return data;
		}
		public void setData(Object data) {
			this.data = data;
		}
        
    }
    
}
