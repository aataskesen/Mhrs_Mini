package tr.mhrs.auth.dto;

public class AuthResponse {
    
    private boolean success;
    private String message;
    private Long userId;
    private String fullName;
    private String token;
    
    // Başarılı giriş için
    public AuthResponse(Long userId, String fullName) {
        this.success = true;
        this.message = "Giriş başarılı";
        this.userId = userId;
        this.fullName = fullName;
        this.token = "TEMP-TOKEN-" + userId;
    }
    
    // Hata durumu için
    public AuthResponse(String errorMessage) {
        this.success = false;
        this.message = errorMessage;
        this.userId = null;
        this.fullName = null;
        this.token = null;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
