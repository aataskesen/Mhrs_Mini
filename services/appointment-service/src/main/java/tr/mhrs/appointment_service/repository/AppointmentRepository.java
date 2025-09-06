package tr.mhrs.appointment_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tr.mhrs.appointment_service.entity.Appointment;
import tr.mhrs.appointment_service.entity.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Randevu kodu ile bul
    Optional<Appointment> findByAppointmentCode(String appointmentCode);

    // TC kimlik no ile randevuları bul
    List<Appointment> findByPatientTcOrderByAppointmentDateDescAppointmentTimeDesc(String patientTc);
    
    // Hasta ID ile randevuları bul (sayfalı)
    Page<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(Long patientId, Pageable pageable);
    
    // Hasta ID ile randevuları bul (sayfalama olmadan)
    List<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(Long patientId);

    // Aktif randevuları bul
    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId AND a.status = :status " +
           "AND (a.appointmentDate > :currentDate OR (a.appointmentDate = :currentDate AND a.appointmentTime > :currentTime)) " +
           "ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    List<Appointment> findActiveAppointments(@Param("patientId") Long patientId,
                                            @Param("status") AppointmentStatus status,
                                            @Param("currentDate") LocalDate currentDate,
                                            @Param("currentTime") LocalTime currentTime);

    // Geçmiş randevuları bul
    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId " +
           "AND (a.appointmentDate < :currentDate OR (a.appointmentDate = :currentDate AND a.appointmentTime < :currentTime)) " +
           "ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    Page<Appointment> findPastAppointments(@Param("patientId") Long patientId,
                                          @Param("currentDate") LocalDate currentDate,
                                          @Param("currentTime") LocalTime currentTime,
                                          Pageable pageable);

    // Doktorun belirli tarihteki randevuları
    List<Appointment> findByDoctorIdAndAppointmentDateOrderByAppointmentTime(Long doctorId, LocalDate date);

    // Doktorun belirli tarih ve saatteki randevusu var mı?
    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
            Long doctorId, LocalDate date, LocalTime time, List<AppointmentStatus> statuses);

    // Hastanın aynı gün aynı bölümde randevusu var mı? (Çift randevu engelleme)
    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
           "WHERE a.patientId = :patientId " +
           "AND a.appointmentDate = :date " +
           "AND a.policlinic.id = :policlinicId " +
           "AND a.status IN :statuses")
    boolean hasAppointmentOnSameDayAndPoliclinic(@Param("patientId") Long patientId,
                                                 @Param("date") LocalDate date,
                                                 @Param("policlinicId") Long policlinicId,
                                                 @Param("statuses") List<AppointmentStatus> statuses);

    // Hastanın belirli tarihteki randevu sayısı (günlük limit kontrolü)
    @Query("SELECT COUNT(a) FROM Appointment a " +
           "WHERE a.patientId = :patientId " +
           "AND a.appointmentDate = :date " +
           "AND a.status IN :statuses")
    long countDailyAppointments(@Param("patientId") Long patientId,
                               @Param("date") LocalDate date,
                               @Param("statuses") List<AppointmentStatus> statuses);

    // İptal edilmemiş randevuları say
    @Query("SELECT COUNT(a) FROM Appointment a " +
           "WHERE a.patientId = :patientId " +
           "AND a.status = 'SCHEDULED' " +
           "AND a.appointmentDate >= :currentDate")
    long countActiveAppointmentsForPatient(@Param("patientId") Long patientId,
                                          @Param("currentDate") LocalDate currentDate);

    // Hatırlatma gönderilecek randevular (1 gün önce)
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.status = 'SCHEDULED' " +
           "AND a.reminderSent = false " +
           "AND a.appointmentDate = :tomorrowDate")
    List<Appointment> findAppointmentsForReminder(@Param("tomorrowDate") LocalDate tomorrowDate);

    // Doktorun aylık randevu istatistikleri
    @Query("SELECT DATE(a.appointmentDate) as date, COUNT(a) as count " +
           "FROM Appointment a " +
           "WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDate BETWEEN :startDate AND :endDate " +
           "AND a.status IN ('SCHEDULED', 'COMPLETED') " +
           "GROUP BY DATE(a.appointmentDate)")
    List<Object[]> getDoctorMonthlyStatistics(@Param("doctorId") Long doctorId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    // Hastanenin günlük randevu sayısı
    @Query("SELECT COUNT(a) FROM Appointment a " +
           "WHERE a.hospital.id = :hospitalId " +
           "AND a.appointmentDate = :date " +
           "AND a.status IN :statuses")
    long countHospitalDailyAppointments(@Param("hospitalId") Long hospitalId,
                                       @Param("date") LocalDate date,
                                       @Param("statuses") List<AppointmentStatus> statuses);

    // Poliklinik bazlı randevu arama
    Page<Appointment> findByPoliclinicIdAndAppointmentDateBetween(
            Long policlinicId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Bekleyen randevuları bul (no-show kontrolü için)
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.status = 'SCHEDULED' " +
           "AND a.appointmentDate = :currentDate " +
           "AND a.appointmentTime < :cutoffTime " +
           "AND a.patientArrived = false")
    List<Appointment> findMissedAppointments(@Param("currentDate") LocalDate currentDate,
                                            @Param("cutoffTime") LocalTime cutoffTime);

    // Doktorun belirli zaman aralığındaki randevuları
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDate = :date " +
           "AND a.appointmentTime BETWEEN :startTime AND :endTime " +
           "AND a.status IN :statuses")
    List<Appointment> findDoctorAppointmentsInTimeRange(@Param("doctorId") Long doctorId,
                                                       @Param("date") LocalDate date,
                                                       @Param("startTime") LocalTime startTime,
                                                       @Param("endTime") LocalTime endTime,
                                                       @Param("statuses") List<AppointmentStatus> statuses);

    // Batch update için randevuları getir
    @Query("SELECT a FROM Appointment a WHERE a.id IN :ids")
    List<Appointment> findByIds(@Param("ids") List<Long> ids);

    // Doktor değişikliği için randevuları bul
    List<Appointment> findByDoctorIdAndStatusAndAppointmentDateGreaterThanEqual(
            Long doctorId, AppointmentStatus status, LocalDate date);
}
