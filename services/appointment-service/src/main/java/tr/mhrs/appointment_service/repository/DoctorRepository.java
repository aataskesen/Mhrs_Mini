package tr.mhrs.appointment_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tr.mhrs.appointment_service.entity.Doctor;
import tr.mhrs.appointment_service.entity.DoctorStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // TC ile doktor bul
    Optional<Doctor> findByTcNo(String tcNo);

    // Diploma no ile doktor bul
    Optional<Doctor> findByDiplomaNo(String diplomaNo);

    // Aktif doktorları getir
    List<Doctor> findByStatus(DoctorStatus status);

    // Hastane ve uzmanlık alanına göre doktorları bul
    @Query("SELECT d FROM Doctor d " +
           "WHERE d.hospital.id = :hospitalId " +
           "AND d.speciality.id = :specialityId " +
           "AND d.status = :status " +
           "ORDER BY d.firstName, d.lastName")
    List<Doctor> findByHospitalAndSpeciality(@Param("hospitalId") Long hospitalId,
                                            @Param("specialityId") Long specialityId,
                                            @Param("status") DoctorStatus status);

    // Hastanedeki tüm doktorlar
    Page<Doctor> findByHospitalIdAndStatus(Long hospitalId, DoctorStatus status, Pageable pageable);

    // Poliklinikteki doktorlar
    @Query("SELECT DISTINCT d FROM Doctor d " +
           "JOIN d.policlinics p " +
           "WHERE p.id = :policlinicId " +
           "AND d.status = :status")
    List<Doctor> findByPoliclinicAndStatus(@Param("policlinicId") Long policlinicId,
                                          @Param("status") DoctorStatus status);

    // İsim ile arama (ad veya soyad)
    @Query("SELECT d FROM Doctor d " +
           "WHERE (LOWER(d.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND d.status = :status")
    Page<Doctor> searchByName(@Param("searchTerm") String searchTerm,
                             @Param("status") DoctorStatus status,
                             Pageable pageable);

    // Uzmanlık alanına göre doktor sayısı
    @Query("SELECT d.speciality.name, COUNT(d) FROM Doctor d " +
           "WHERE d.hospital.id = :hospitalId " +
           "AND d.status = 'ACTIVE' " +
           "GROUP BY d.speciality.name")
    List<Object[]> countDoctorsBySpecialityInHospital(@Param("hospitalId") Long hospitalId);

    // Şehirdeki doktorlar
    @Query("SELECT d FROM Doctor d " +
           "WHERE d.hospital.city.id = :cityId " +
           "AND d.status = :status")
    Page<Doctor> findByCityAndStatus(@Param("cityId") Long cityId,
                                    @Param("status") DoctorStatus status,
                                    Pageable pageable);

    // İlçedeki doktorlar
    @Query("SELECT d FROM Doctor d " +
           "WHERE d.hospital.district.id = :districtId " +
           "AND d.status = :status")
    Page<Doctor> findByDistrictAndStatus(@Param("districtId") Long districtId,
                                        @Param("status") DoctorStatus status,
                                        Pageable pageable);

    // Birden fazla hastanede çalışan doktorlar (özel muayenehane dahil)
    @Query("SELECT d FROM Doctor d " +
           "WHERE d.tcNo IN (" +
           "  SELECT d2.tcNo FROM Doctor d2 " +
           "  GROUP BY d2.tcNo " +
           "  HAVING COUNT(DISTINCT d2.hospital.id) > 1" +
           ")")
    List<Doctor> findDoctorsWorkingInMultipleHospitals();

    // Unvana göre doktorlar
    List<Doctor> findByTitleContainingIgnoreCaseAndStatus(String title, DoctorStatus status);
    
    // Uzmanlık alanına göre doktorları bul
    List<Doctor> findBySpecialityId(Long specialityId);
    
    // İsimde arama (fullName field'ı yok, firstName ve lastName'de arama yapalım)
    @Query("SELECT d FROM Doctor d " +
           "WHERE (LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND d.status = 'ACTIVE'")
    List<Doctor> findByFullNameContainingIgnoreCase(@Param("name") String name);

    // Email ile doktor bul
    Optional<Doctor> findByEmail(String email);

    // Telefon ile doktor bul
    Optional<Doctor> findByPhone(String phone);

    // Müsait doktorları bul (randevu sayısı az olanlar)
    @Query(value = "SELECT d.* FROM doctors d " +
           "LEFT JOIN (" +
           "  SELECT doctor_id, COUNT(*) as appointment_count " +
           "  FROM appointments " +
           "  WHERE appointment_date = :date " +
           "  AND status = 'SCHEDULED' " +
           "  GROUP BY doctor_id" +
           ") a ON d.id = a.doctor_id " +
           "WHERE d.hospital_id = :hospitalId " +
           "AND d.speciality_id = :specialityId " +
           "AND d.status = 'ACTIVE' " +
           "ORDER BY COALESCE(a.appointment_count, 0) ASC",
           nativeQuery = true)
    List<Doctor> findAvailableDoctors(@Param("hospitalId") Long hospitalId,
                                     @Param("specialityId") Long specialityId,
                                     @Param("date") String date);

    // Yakında doğum günü olan doktorlar (kutlama için)
    @Query("SELECT d FROM Doctor d " +
           "WHERE MONTH(d.birthDate) = :month " +
           "AND DAY(d.birthDate) BETWEEN :startDay AND :endDay")
    List<Doctor> findUpcomingBirthdays(@Param("month") int month,
                                      @Param("startDay") int startDay,
                                      @Param("endDay") int endDay);
}
