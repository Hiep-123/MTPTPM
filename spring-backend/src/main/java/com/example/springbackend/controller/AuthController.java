package com.example.springbackend.controller;

import com.example.springbackend.model.User;
import com.example.springbackend.repository.UserRepository;
import com.example.springbackend.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        try {
            String userName = (String) body.get("userName");
            String password = (String) body.get("password");
            String email = (String) body.getOrDefault("email", "");
            String phone = (String) body.getOrDefault("phone", "");

            if (userName == null || password == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Tên đăng nhập và mật khẩu không được để trống!"));
            }

            Optional<User> existing = userRepository.findAll().stream().filter(u -> userName.equals(u.getUserName()))
                    .findFirst();
            if (existing.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tên đăng nhập đã tồn tại!"));
            }

            User newUser = new User();
            newUser.setUserName(userName);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setEmail(email);
            newUser.setName(userName);
            newUser.setPhone(phone);
            newUser.setRole("user");

            User saved = userRepository.save(newUser);
            System.out.println("Saved user id: " + saved.getId());
            return ResponseEntity.status(201).body(Map.of("message", "Đăng ký thành công!", "id", saved.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        try {
            String userName = (String) body.get("userName");
            String password = (String) body.get("password");

            Optional<User> userOpt = userRepository.findAll().stream().filter(u -> userName.equals(u.getUserName()))
                    .findFirst();
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tên đăng nhập hoặc mật khẩu không đúng"));
            }
            User user = userOpt.get();
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tên đăng nhập hoặc mật khẩu không đúng"));
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("role", user.getRole());
            String token = jwtUtil.generateToken(claims);

            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Đăng nhập thành công");
            resp.put("token", token);
            resp.put("user", Map.of("_id", user.getId(), "userName", user.getUserName(), "email", user.getEmail(),
                    "phone", user.getPhone(), "role", user.getRole()));
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
