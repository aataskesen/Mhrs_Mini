package tr.mhrs.appointment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tr.mhrs.appointment_service.entity.TimeSlot;
import tr.mhrs.appointment_service.entity.SlotStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    // 1️⃣ DOKTORUN BELİRLİ GÜNDEKİ TÜM SLOTLARI
    List<TimeSlot> findByDoctorIdAndSlotDate(Long doctorId, LocalDate slotDate);

    // 2️⃣ DOKTORUN BELİRLİ GÜNDEKİ BOŞ SLOTLARI
    List<TimeSlot> findByDoctorIdAndSlotDateAndStatus(
            Long doctorId,
            LocalDate slotDate,
            SlotStatus status
    );

    // 3️⃣ BELİRLİ SAAT ARALIĞINDAKİ SLOTLAR
    @Query("SELECT ts FROM TimeSlot ts " +
            "WHERE ts.doctor.id = :doctorId " +
            "AND ts.slotDate = :date " +
            "AND ts.slotTime BETWEEN :startTime AND :endTime " +
            "AND ts.status = :status " +
            "ORDER BY ts.slotTime")
    List<TimeSlot> findDoctorAvailableSlotsBetweenTimes(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("status") SlotStatus status
    );

    // 4️⃣ SPESİFİK SLOT KONTROLÜ
    Optional<TimeSlot> findByDoctorIdAndSlotDateAndSlotTime(
            Long doctorId,
            LocalDate slotDate,
            LocalTime slotTime
    );

    // 5️⃣ HASTANIN RANDEVU SLOTLARI
    @Query("SELECT ts FROM TimeSlot ts " +
            "WHERE ts.appointment.patientId = :patientId " +
            "AND ts.slotDate >= :fromDate " +
            "ORDER BY ts.slotDate, ts.slotTime")
    List<TimeSlot> findPatientBookedSlots(
            @Param("patientId") Long patientId,
            @Param("fromDate") LocalDate fromDate
    );

    // 6️⃣ DOKTORUN DOLULUK ORANI
    @Query("SELECT " +
            "COUNT(CASE WHEN ts.status = 'BOOKED' THEN 1 END) * 100.0 / COUNT(*) " +
            "FROM TimeSlot ts " +
            "WHERE ts.doctor.id = :doctorId " +
            "AND ts.slotDate = :date")
    Double calculateDoctorOccupancyRate(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date
    );

    // 7️⃣ EN ÇOK TERCİH EDİLEN SAATLER (hospital yerine doctor.hospital kullan)
    @Query("SELECT ts.slotTime, COUNT(ts) as bookingCount " +
            "FROM TimeSlot ts " +
            "WHERE ts.status = 'BOOKED' " +
            "AND ts.doctor.hospital.id = :hospitalId " +
            "AND ts.slotDate BETWEEN :startDate AND :endDate " +
            "GROUP BY ts.slotTime " +
            "ORDER BY COUNT(ts) DESC")
    List<Object[]> findMostPreferredTimeSlots(
            @Param("hospitalId") Long hospitalId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 8️⃣ SLOT DURUMUNU GÜNCELLE
    @Modifying
    @Transactional
    @Query("UPDATE TimeSlot ts " +
            "SET ts.status = :newStatus, " +
            "ts.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE ts.id = :slotId")
    int updateSlotStatus(
            @Param("slotId") Long slotId,
            @Param("newStatus") SlotStatus newStatus
    );

    // 9️⃣ ESKİ SLOTLARI TEMİZLE
    @Modifying
    @Transactional
    @Query("DELETE FROM TimeSlot ts " +
            "WHERE ts.slotDate < :cutoffDate " +
            "AND ts.status = 'AVAILABLE'")
    int deleteOldAvailableSlots(@Param("cutoffDate") LocalDate cutoffDate);

    // 🔟 DOKTORUN İLK MÜSAİT SLOTU
    @Query("SELECT ts FROM TimeSlot ts " +
            "WHERE ts.doctor.id = :doctorId " +
            "AND ts.status = 'AVAILABLE' " +
            "AND ts.slotDate >= :fromDate " +
            "AND (ts.slotDate > :fromDate OR ts.slotTime > :fromTime) " +
            "ORDER BY ts.slotDate, ts.slotTime")
    Optional<TimeSlot> findNextAvailableSlot(
            @Param("doctorId") Long doctorId,
            @Param("fromDate") LocalDate fromDate,
            @Param("fromTime") LocalTime fromTime
    );

    // 1️⃣1️⃣ ÇAKIŞAN SLOTLARI BUL
    @Query("SELECT ts FROM TimeSlot ts " +
            "WHERE ts.doctor.id = :doctorId " +
            "AND ts.slotDate = :date " +
            "AND ts.status = 'BOOKED' " +
            "AND ts.slotTime BETWEEN :startTime AND :endTime")
    List<TimeSlot> findConflictingSlots(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    // 1️⃣2️⃣ HASTANENİN BOŞ SLOT SAYISI
    @Query("SELECT COUNT(ts) FROM TimeSlot ts " +
            "WHERE ts.doctor.hospital.id = :hospitalId " +
            "AND ts.slotDate = :date " +
            "AND ts.status = 'AVAILABLE'")
    Long countAvailableSlotsByHospital(
            @Param("hospitalId") Long hospitalId,
            @Param("date") LocalDate date
    );

    // 1️⃣3️⃣ POLİKLİNİĞE GÖRE BOŞ SLOTLAR
    @Query("SELECT ts FROM TimeSlot ts " +
            "WHERE ts.policlinic.id = :policlinicId " +
            "AND ts.slotDate = :date " +
            "AND ts.status = 'AVAILABLE' " +
            "ORDER BY ts.slotTime")
    List<TimeSlot> findAvailableSlotsByPoliclinic(
            @Param("policlinicId") Long policlinicId,
            @Param("date") LocalDate date
    );

    // 1️⃣4️⃣ REZERVASYON TIMEOUT OLAN SLOTLARI BUL
    @Query("SELECT ts FROM TimeSlot ts " +
            "WHERE ts.status = 'RESERVED' " +
            "AND ts.reservedUntil < CURRENT_TIMESTAMP")
    List<TimeSlot> findExpiredReservations();

    // 1️⃣5️⃣ ONLINE REZERVASYONa AÇIK SLOTLAR
    List<TimeSlot> findByDoctorIdAndSlotDateAndStatusAndIsOnlineBookable(
            Long doctorId,
            LocalDate slotDate,
            SlotStatus status,
            Boolean isOnlineBookable
    );
}