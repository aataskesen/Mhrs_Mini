package tr.mhrs.auth.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class RegisterRequest {
    
    @NotBlank(message = "TC Kimlik No zorunlu")
    @Size(min = 11, max = 11, message = "TC Kimlik No 11 hane olmalı")
    private String tcNo;
    
    @NotBlank(message = "Ad zorunlu")
    private String firstName;
    
    @NotBlank(message = "Soyad zorunlu")
    private String lastName;
    
    @NotBlank(message = "Email zorunlu")
    @Email(message = "Geçerli bir email giriniz")
    private String email;
    
    @NotBlank(message = "Telefon zorunlu")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Geçerli telefon numarası giriniz")
    private String phoneNumber;
    
    @NotNull(message = "Doğum tarihi zorunlu")
    @Past(message = "Doğum tarihi geçmiş bir tarih olmalı")
    private LocalDate birthDate;
    
    @NotBlank(message = "Şifre zorunlu")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalı")
    private String password;
    
    @NotBlank(message = "Cinsiyet zorunlu")
    private String gender;
    
    public RegisterRequest() {}
    
    // Getters and Setters
    public String getTcNo() { return tcNo; }
    public void setTcNo(String tcNo) { this.tcNo = tcNo; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
