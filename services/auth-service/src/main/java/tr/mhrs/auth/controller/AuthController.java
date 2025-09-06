package tr.mhrs.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.mhrs.auth.dto.AuthResponse;
import tr.mhrs.auth.dto.LoginRequest;
import tr.mhrs.auth.dto.RegisterRequest;
import tr.mhrs.auth.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    // Manuel constructor (Lombok çalışmıyorsa)
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    // TEST ENDPOINT
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth Service Çalışıyor!");
    }
    
    // KULLANICI KAYDI
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse("Hata: " + e.getMessage()));  // Düzeltildi
        }
    }
    
    // KULLANICI GİRİŞİ
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse("Hata: " + e.getMessage()));  // Düzeltildi
        }
    }
    
    // KULLANICI BİLGİSİ
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        try {
            var user = authService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // TC İLE KULLANICI BİLGİSİ
    @GetMapping("/user/tc/{tcNo}")
    public ResponseEntity<?> getUserByTc(@PathVariable String tcNo) {
        try {
            var user = authService.getUserByTcNo(tcNo);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}