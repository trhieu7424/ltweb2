package com.ltweb2.shop.controller;

import com.ltweb2.shop.dto.RegisterRequestDTO;
import com.ltweb2.shop.entity.Role;
import com.ltweb2.shop.entity.User;
import com.ltweb2.shop.repository.RoleRepository;
import com.ltweb2.shop.repository.UserRepository;
import com.ltweb2.shop.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, 
                          UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- API ĐĂNG NHẬP  ---
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        Map<String, String> response = new HashMap<>();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            String jwtToken = tokenProvider.generateToken(authentication.getName());

            response.put("status", "Thành công");
            response.put("message", "Đăng nhập thành công!");
            response.put("email", authentication.getName());
            response.put("accessToken", jwtToken);
            
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            response.put("status", "Thất bại");
            response.put("message", "Email hoặc mật khẩu không chính xác!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("status", "Lỗi");
            response.put("message", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // --- API KIỂM TRA PROFILE (Giữ nguyên như cũ) ---
    @GetMapping("/me")
    public ResponseEntity<String> getMyProfile() {
        return ResponseEntity.ok("Chúc mừng! Bạn đã dùng Token truy cập thành công API bảo mật.");
    }

    // --- API MỚI: ĐĂNG KÝ TÀI KHOẢN ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        // 1. Kiểm tra xem email đã có ai dùng chưa
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email này đã được sử dụng!"));
        }

        // 2. Tạo đối tượng User mới
        User user = new User();
        user.setEmail(request.getEmail());
        // Mã hóa mật khẩu trước khi lưu vào DB
        user.setPassword(passwordEncoder.encode(request.getPassword())); 
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setIsActive(true); // Tài khoản mới tạo mặc định được kích hoạt

        // 3. Gán quyền mặc định là ROLE_CUSTOMER
        Role userRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy quyền ROLE_CUSTOMER trong DB."));
        user.getRoles().add(userRole);

        // 4. Lưu xuống Database
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ."));
    }
}