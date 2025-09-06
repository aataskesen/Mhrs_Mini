package tr.mhrs.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users",
       indexes = {
           @Index(name = "idx_tc_no", columnList = "tc_no", unique = true),
           @Index(name = "idx_email", columnList = "email", unique = true),
           @Index(name = "idx_phone", columnList = "phone_number")
       })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tc_no", unique = true, nullable = false, length = 11)
    private String tcNo; // TC Kimlik Numarası

    @Column(name = "password", nullable = false)
    private String password; // BCrypt encrypted

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false, length = 150)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber; // Format: +905551234567

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "district_id")
    private District district;

    // Kullanıcı rolleri
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", 
                     joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    // Hesap durumu
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false; // Email/SMS doğrulaması

    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked = false;

    // OTP ve güvenlik
    @Column(name = "otp_code", length = 6)
    private String otpCode;

    @Column(name = "otp_expiry_time")
    private LocalDateTime otpExpiryTime;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    // Audit fields
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "created_by", length = 100)
    private String createdBy = "SYSTEM";

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    // Bildirim tercihleri
    @Column(name = "sms_notifications_enabled")
    private Boolean smsNotificationsEnabled = true;

    @Column(name = "email_notifications_enabled")
    private Boolean emailNotificationsEnabled = true;

    // Refresh token for JWT
    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "refresh_token_expiry")
    private LocalDateTime refreshTokenExpiry;

    // Password reset
    @Column(name = "reset_token", length = 100)
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    // HES kodu (Pandemi durumları için)
    @Column(name = "hes_code", length = 12)
    private String hesCode;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public User() {
        this.roles.add(Role.USER);
    }

    public User(String tcNo, String firstName, String lastName, 
                String email, String phoneNumber, LocalDate birthDate, Gender gender) {
        this.tcNo = tcNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.gender = gender;
        this.roles.add(Role.USER);
    }

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAccountNonLocked() {
        if (!isLocked) {
            return true;
        }
        // Kilit süresi dolmuş mu kontrol et (30 dakika)
        if (lockTime != null && lockTime.plusMinutes(30).isBefore(LocalDateTime.now())) {
            this.isLocked = false;
            this.failedLoginAttempts = 0;
            this.lockTime = null;
            return true;
        }
        return false;
    }

    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 3) {
            this.isLocked = true;
            this.lockTime = LocalDateTime.now();
        }
    }

    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.isLocked = false;
        this.lockTime = null;
    }

    public boolean isOtpValid(String inputOtp) {
        if (otpCode == null || otpExpiryTime == null) {
            return false;
        }
        return otpCode.equals(inputOtp) && 
               LocalDateTime.now().isBefore(otpExpiryTime);
    }

    // Enums
    public enum Gender {
        MALE("Erkek"),
        FEMALE("Kadın"),
        OTHER("Diğer");

        private final String displayName;

        Gender(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Role {
        USER("Vatandaş"),
        DOCTOR("Doktor"),
        ADMIN("Sistem Yöneticisi"),
        HOSPITAL_ADMIN("Hastane Yöneticisi"),
        SUPPORT("Destek Personeli");

        private final String displayName;

        Role(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // City ve District entity'leri (auth-service'de de olmalı)
    @Entity
    @Table(name = "cities")
    public static class City {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "name", nullable = false, length = 100)
        private String name;

        @Column(name = "plate_code", nullable = false)
        private Integer plateCode;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getPlateCode() { return plateCode; }
        public void setPlateCode(Integer plateCode) { this.plateCode = plateCode; }
    }

    @Entity
    @Table(name = "districts")
    public static class District {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "name", nullable = false, length = 100)
        private String name;

        @ManyToOne
        @JoinColumn(name = "city_id", nullable = false)
        private City city;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public City getCity() { return city; }
        public void setCity(City city) { this.city = city; }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTcNo() { return tcNo; }
    public void setTcNo(String tcNo) { this.tcNo = tcNo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

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

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public District getDistrict() { return district; }
    public void setDistrict(District district) { this.district = district; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public Boolean getIsLocked() { return isLocked; }
    public void setIsLocked(Boolean isLocked) { this.isLocked = isLocked; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public LocalDateTime getOtpExpiryTime() { return otpExpiryTime; }
    public void setOtpExpiryTime(LocalDateTime otpExpiryTime) { this.otpExpiryTime = otpExpiryTime; }

    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public LocalDateTime getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(LocalDateTime lastLoginTime) { this.lastLoginTime = lastLoginTime; }

    public LocalDateTime getLockTime() { return lockTime; }
    public void setLockTime(LocalDateTime lockTime) { this.lockTime = lockTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public Boolean getSmsNotificationsEnabled() { return smsNotificationsEnabled; }
    public void setSmsNotificationsEnabled(Boolean smsNotificationsEnabled) { this.smsNotificationsEnabled = smsNotificationsEnabled; }

    public Boolean getEmailNotificationsEnabled() { return emailNotificationsEnabled; }
    public void setEmailNotificationsEnabled(Boolean emailNotificationsEnabled) { this.emailNotificationsEnabled = emailNotificationsEnabled; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public LocalDateTime getRefreshTokenExpiry() { return refreshTokenExpiry; }
    public void setRefreshTokenExpiry(LocalDateTime refreshTokenExpiry) { this.refreshTokenExpiry = refreshTokenExpiry; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public LocalDateTime getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }

    public String getHesCode() { return hesCode; }
    public void setHesCode(String hesCode) { this.hesCode = hesCode; }
}
