package tr.mhrs.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
    
    @NotBlank(message = "TC Kimlik No boş olamaz")
    @Size(min = 11, max = 11, message = "TC Kimlik No 11 haneli olmalıdır")
    private String tcNo;
    
    @NotBlank(message = "Şifre boş olamaz")
    private String password;
    
    public LoginRequest() {}
    
    public LoginRequest(String tcNo, String password) {
        this.tcNo = tcNo;
        this.password = password;
    }
    
    public String getTcNo() { 
        return tcNo; 
    }
    
    public void setTcNo(String tcNo) { 
        this.tcNo = tcNo; 
    }
    
    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }
}
