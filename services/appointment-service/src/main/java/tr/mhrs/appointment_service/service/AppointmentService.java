package tr.mhrs.appointment_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.mhrs.appointment_service.dto.AppointmentResponse;
import tr.mhrs.appointment_service.dto.BookingRequest;
import tr.mhrs.appointment_service.entity.*;
import tr.mhrs.appointment_service.exception.BusinessException;
import tr.mhrs.appointment_service.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentService {

    // Repository'ler
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    
    // Manuel constructor
    public AppointmentService(AppointmentRepository appointmentRepository,
                            DoctorRepository doctorRepository,
                            HospitalRepository hospitalRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
    }

    /**
     * RANDEVU ALMA
     */
    public AppointmentResponse bookAppointment(BookingRequest request) {

        // 1. İş Kuralı: Geçmiş tarihe randevu alınamaz
        if (request.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Geçmiş tarihe randevu alınamaz");
        }

        // 2. İş Kuralı: En fazla 30 gün sonraya randevu alınabilir
        if (request.getAppointmentDate().isAfter(LocalDate.now().plusDays(30))) {
            throw new BusinessException("En fazla 30 gün sonraya randevu alabilirsiniz");
        }

        // 3. Doktor kontrolü
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new BusinessException("Doktor bulunamadı"));

        // 4. Doktor aktif mi?
        if (doctor.getStatus() != DoctorStatus.ACTIVE) {
            throw new BusinessException("Bu doktor şu anda randevu vermiyor");
        }

        // 5. Hastane kontrolü
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new BusinessException("Hastane bulunamadı"));

        // 6. Slot müsaitlik kontrolü - Bu saatte başka randevu var mı?
        boolean slotTaken = appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
                request.getDoctorId(),
                request.getAppointmentDate(),
                request.getAppointmentTime(),
                List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)
        );

        if (slotTaken) {
            throw new BusinessException("Bu saat dolu, lütfen başka bir saat seçin");
        }

        // 7. İş Kuralı: Hasta aynı güne aynı bölümden randevu alamaz
        long dailyAppointmentCount = appointmentRepository.countDailyAppointments(
                request.getPatientId(),
                request.getAppointmentDate(),
                List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)
        );

        if (dailyAppointmentCount >= 3) {
            throw new BusinessException("Aynı güne en fazla 3 randevu alabilirsiniz");
        }

        // 8. İş Kuralı: Hasta toplam 5'ten fazla aktif randevuya sahip olamaz
        long activeAppointmentCount = appointmentRepository.countActiveAppointmentsForPatient(
                request.getPatientId(),
                LocalDate.now()
        );

        if (activeAppointmentCount >= 5) {
            throw new BusinessException("En fazla 5 aktif randevunuz olabilir");
        }

        // 9. Randevu oluştur
        Appointment appointment = new Appointment();
        appointment.setPatientId(request.getPatientId());
        appointment.setPatientTc(request.getPatientTc());
        appointment.setPatientName(request.getPatientName());
        appointment.setDoctor(doctor);
        appointment.setHospital(hospital);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setNotes(request.getNotes());
        appointment.setAppointmentCode(generateAppointmentCode());

        // 10. Kaydet
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // 11. Response oluştur
        AppointmentResponse response = AppointmentResponse.builder()
                .success(true)
                .message("Randevu başarıyla oluşturuldu")
                .appointmentCode(savedAppointment.getAppointmentCode())
                .patientName(savedAppointment.getPatientName())
                .doctorName(doctor.getFullName())
                .hospitalName(hospital.getName())
                .appointmentDate(savedAppointment.getAppointmentDate())
                .appointmentTime(savedAppointment.getAppointmentTime())
                .status(savedAppointment.getStatus().toString())
                .build();

        return response;
    }

    /**
     * RANDEVU İPTAL
     */
    public AppointmentResponse cancelAppointment(String appointmentCode, String cancellationReason) {

        // 1. Randevuyu bul
        Appointment appointment = appointmentRepository.findByAppointmentCode(appointmentCode)
                .orElseThrow(() -> new BusinessException("Randevu bulunamadı"));

        // 2. Zaten iptal edilmiş mi?
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessException("Bu randevu zaten iptal edilmiş");
        }

        // 3. Geçmiş randevu iptal edilemez
        if (appointment.isPast()) {
            throw new BusinessException("Geçmiş randevular iptal edilemez");
        }

        // 4. İş Kuralı: Randevu saatine 30 dakikadan az kaldıysa iptal edilemez
        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime()
        );

        if (appointmentDateTime.minusMinutes(30).isBefore(LocalDateTime.now())) {
            throw new BusinessException("Randevu saatine 30 dakikadan az kaldığı için iptal edilemez");
        }

        // 5. İptal et
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(cancellationReason);
        appointment.setCancelledAt(LocalDateTime.now());
        appointment.setCancelledBy("PATIENT");

        appointmentRepository.save(appointment);

        // 6. Response
        return AppointmentResponse.builder()
                .success(true)
                .message("Randevu başarıyla iptal edildi")
                .appointmentCode(appointmentCode)
                .status("CANCELLED")
                .build();
    }

    /**
     * HASTA RANDEVULARINI LİSTELE
     */
    public List<AppointmentResponse> getMyAppointments(Long patientId) {

        // Aktif randevuları getir
        List<Appointment> appointments = appointmentRepository.findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(patientId)
        ;

        // DTO'ya çevir
        return appointments.stream()
                .map(appointment -> AppointmentResponse.builder()
                        .success(true)
                        .appointmentCode(appointment.getAppointmentCode())
                        .patientName(appointment.getPatientName())
                        .doctorName(appointment.getDoctor().getFullName())
                        .hospitalName(appointment.getHospital().getName())
                        .appointmentDate(appointment.getAppointmentDate())
                        .appointmentTime(appointment.getAppointmentTime())
                        .status(appointment.getStatus().toString())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * TC NO İLE RANDEVU SORGULA
     */
    public List<AppointmentResponse> getAppointmentsByTc(String tcNo) {

        List<Appointment> appointments = appointmentRepository
                .findByPatientTcOrderByAppointmentDateDescAppointmentTimeDesc(tcNo);

        return appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * RANDEVU KODU OLUŞTUR
     */
    private String generateAppointmentCode() {
        // RND + YIL + AY + GUN + SAAT + DAKIKA + SANIYE
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        return "RND" + timestamp;
    }

    /**
     * Entity'yi Response'a çevir (Helper method)
     */
    private AppointmentResponse convertToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .success(true)
                .appointmentCode(appointment.getAppointmentCode())
                .patientName(appointment.getPatientName())
                .patientTc(appointment.getPatientTc())
                .doctorName(appointment.getDoctor().getFullName())
                .hospitalName(appointment.getHospital().getName())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus().toString())
                .build();
    }
}