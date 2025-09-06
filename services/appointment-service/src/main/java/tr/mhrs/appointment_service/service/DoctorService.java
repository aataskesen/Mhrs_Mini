package tr.mhrs.appointment_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.mhrs.appointment_service.entity.Doctor;
import tr.mhrs.appointment_service.entity.DoctorStatus;
import tr.mhrs.appointment_service.exception.BusinessException;
import tr.mhrs.appointment_service.repository.DoctorRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)  // Sadece okuma işlemleri için optimize
public class DoctorService {

    private final DoctorRepository doctorRepository;
    
    // Manuel constructor (Lombok çalışmıyorsa)
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    /**
     * HASTANEDEKİ DOKTORLARI LİSTELE
     */
    public List<Doctor> findDoctorsByHospital(Long hospitalId) {
        // hospitalId null kontrolü
        if (hospitalId == null) {
            throw new BusinessException("Hastane ID boş olamaz");
        }

        // Sadece aktif doktorları getir
        return doctorRepository.findByHospitalIdAndStatus(
                hospitalId,
                DoctorStatus.ACTIVE,
                null  // Pageable kullanmıyoruz şimdilik
        ).getContent();  // Page'den List'e çevir
    }

    /**
     * UZMANLIK ALANINA GÖRE DOKTORLARI LİSTELE
     */
    public List<Doctor> findDoctorsBySpeciality(Long specialityId) {
        // specialityId null kontrolü
        if (specialityId == null) {
            throw new BusinessException("Uzmanlık alanı ID boş olamaz");
        }

        // Repository'deki metodu çağır
        return doctorRepository.findBySpecialityId(specialityId);
    }

    /**
     * İSİMLE DOKTOR ARA
     */
    public List<Doctor> searchDoctors(String name) {
        // İsim boş mu kontrolü
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("Arama terimi boş olamaz");
        }

        // En az 2 karakter olmalı
        if (name.trim().length() < 2) {
            throw new BusinessException("Arama terimi en az 2 karakter olmalı");
        }

        // İsimle arama yap (büyük/küçük harf duyarsız)
        return doctorRepository.findByFullNameContainingIgnoreCase(name.trim());
    }

    /**
     * DOKTOR DETAY BİLGİSİ
     */
    public Doctor getDoctorDetail(Long doctorId) {
        // ID null kontrolü
        if (doctorId == null) {
            throw new BusinessException("Doktor ID boş olamaz");
        }

        // Doktoru bul veya hata fırlat
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessException("Doktor bulunamadı. ID: " + doctorId));

        // Doktor aktif değilse uyarı
        if (doctor.getStatus() != DoctorStatus.ACTIVE) {
            throw new BusinessException("Bu doktor şu anda aktif değil");
        }

        return doctor;
    }

    /**
     * HASTANE VE UZMANLIĞA GÖRE DOKTOR BUL
     */
    public List<Doctor> findDoctorsByHospitalAndSpeciality(Long hospitalId, Long specialityId) {
        // Parametreleri kontrol et
        if (hospitalId == null || specialityId == null) {
            throw new BusinessException("Hastane ID ve Uzmanlık ID zorunludur");
        }

        // Repository metodunu çağır
        return doctorRepository.findByHospitalAndSpeciality(
                hospitalId,
                specialityId,
                DoctorStatus.ACTIVE
        );
    }

    /**
     * TC NO İLE DOKTOR BUL
     */
    public Doctor findDoctorByTcNo(String tcNo) {
        // TC kontrolü
        if (tcNo == null || tcNo.length() != 11) {
            throw new BusinessException("Geçerli bir TC Kimlik No giriniz");
        }

        // TC ile doktor bul
        return doctorRepository.findByTcNo(tcNo)
                .orElseThrow(() -> new BusinessException("Bu TC No ile kayıtlı doktor bulunamadı"));
    }

    /**
     * TÜM AKTİF DOKTORLARI LİSTELE
     */
    public List<Doctor> getAllActiveDoctors() {
        return doctorRepository.findByStatus(DoctorStatus.ACTIVE);
    }

    /**
     * DOKTOR SAYISINI DÖNDÜR (İstatistik için)
     */
    public long getTotalDoctorCount() {
        return doctorRepository.count();
    }

    /**
     * AKTİF DOKTOR SAYISI
     */
    public long getActiveDoctorCount() {
        return doctorRepository.findByStatus(DoctorStatus.ACTIVE).size();
    }
}