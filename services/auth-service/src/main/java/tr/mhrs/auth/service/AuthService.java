package tr.mhrs.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.mhrs.auth.dto.AuthResponse;
import tr.mhrs.auth.dto.LoginRequest;
import tr.mhrs.auth.dto.RegisterRequest;
import tr.mhrs.auth.entity.User;
import tr.mhrs.auth.exception.BusinessException;
import tr.mhrs.auth.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // KAYIT (REGISTER) İŞLEMİ
    public AuthResponse register(RegisterRequest request) {

        // 1. TC Kimlik No kontrolü
        if (!isValidTcNo(request.getTcNo())) {
            throw new BusinessException("Geçersiz TC Kimlik No");
        }

        // 2. TC zaten kayıtlı mı?
        if (userRepository.existsByTcNo(request.getTcNo())) {
            throw new BusinessException("Bu TC Kimlik No zaten kayıtlı");
        }

        // 3. Email zaten kullanımda mı?
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Bu email adresi zaten kullanımda");
        }

        // 4. Yeni User oluştur
        User newUser = new User();
        newUser.setTcNo(request.getTcNo());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setEmail(request.getEmail());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setBirthDate(request.getBirthDate());
        newUser.setPassword(request.getPassword()); // Şimdilik plain text
        newUser.setGender(User.Gender.valueOf(request.getGender()));
        newUser.setIsActive(true);
        newUser.setIsVerified(false);

        // 5. Kaydet
        User savedUser = userRepository.save(newUser);

        // 6. Response döndür
        return new AuthResponse(savedUser.getId(), savedUser.getFullName());
    }

    // GİRİŞ (LOGIN) İŞLEMİ
    public AuthResponse login(LoginRequest request) {

        // 1. TC ile kullanıcıyı bul
        User user = userRepository.findByTcNo(request.getTcNo())
                .orElseThrow(() -> new BusinessException("Kullanıcı bulunamadı"));

        // 2. Hesap aktif mi?
        if (!user.getIsActive()) {
            throw new BusinessException("Hesabınız aktif değil");
        }

        // 3. Hesap kilitli mi?
        if (user.getIsLocked()) {
            throw new BusinessException("Hesabınız kilitlenmiş. Lütfen destek ile iletişime geçin");
        }

        // 4. Şifre kontrolü (şimdilik plain text)
        if (!user.getPassword().equals(request.getPassword())) {
            // Hatalı giriş sayısını artır
            user.incrementFailedAttempts();
            userRepository.save(user);

            // 3 hatalı deneme = hesap kilitle
            if (user.getFailedLoginAttempts() >= 3) {
                throw new BusinessException("3 hatalı deneme! Hesabınız kilitlendi");
            }

            throw new BusinessException("Şifre hatalı. Kalan deneme: " +
                    (3 - user.getFailedLoginAttempts()));
        }

        // 5. Başarılı giriş - sayaçları sıfırla
        user.resetFailedAttempts();
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        // 6. Response döndür
        return new AuthResponse(user.getId(), user.getFullName());
    }

    // TC KİMLİK NO DOĞRULAMA
    public boolean isValidTcNo(String tcNo) {
        // Null veya uzunluk kontrolü
        if (tcNo == null || tcNo.length() != 11) {
            return false;
        }

        // Sadece rakam mı?
        if (!tcNo.matches("[0-9]+")) {
            return false;
        }

        // İlk hane 0 olamaz
        if (tcNo.startsWith("0")) {
            return false;
        }

        // TC Kimlik No algoritması (Gerçek algoritma)
        try {
            int[] digits = new int[11];
            for (int i = 0; i < 11; i++) {
                digits[i] = Integer.parseInt(tcNo.substring(i, i + 1));
            }

            // 10. hane kontrolü
            int sumOdd = digits[0] + digits[2] + digits[4] + digits[6] + digits[8];
            int sumEven = digits[1] + digits[3] + digits[5] + digits[7];
            int digit10 = ((sumOdd * 7) - sumEven) % 10;

            if (digits[9] != digit10) {
                return false;
            }

            // 11. hane kontrolü
            int sumAll = 0;
            for (int i = 0; i < 10; i++) {
                sumAll += digits[i];
            }
            int digit11 = sumAll % 10;

            if (digits[10] != digit11) {
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // KULLANICI BİLGİSİ GETIR
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Kullanıcı bulunamadı"));
    }

    // KULLANICI BİLGİSİ GETIR (TC ile)
    public User getUserByTcNo(String tcNo) {
        return userRepository.findByTcNo(tcNo)
                .orElseThrow(() -> new BusinessException("Kullanıcı bulunamadı"));
    }

    // ŞİFRE DEĞİŞTİRME (Bonus)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);

        // Eski şifre doğru mu?
        if (!user.getPassword().equals(oldPassword)) {
            throw new BusinessException("Mevcut şifreniz yanlış");
        }

        // Yeni şifre çok kısa mı?
        if (newPassword.length() < 6) {
            throw new BusinessException("Yeni şifre en az 6 karakter olmalı");
        }

        // Şifreyi güncelle
        user.setPassword(newPassword);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // HESAP AKTİF/PASİF YAPMA (Admin için)
    public void toggleUserStatus(Long userId) {
        User user = getUserById(userId);
        user.setIsActive(!user.getIsActive());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}