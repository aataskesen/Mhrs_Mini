package tr.mhrs.appointment_service.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments", 
       indexes = {
           @Index(name = "idx_appointment_date", columnList = "appointment_date"),
           @Index(name = "idx_patient_id", columnList = "patient_id"),
           @Index(name = "idx_doctor_id", columnList = "doctor_id"),
           @Index(name = "idx_status", columnList = "status")
       })
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_code", unique = true, nullable = false, length = 20)
    private String appointmentCode; // Randevu kodu (örn: RND2024090112345)

    @Column(name = "patient_id", nullable = false)
    private Long patientId; // User entity'si auth-service'de olacak

    @Column(name = "patient_tc", nullable = false, length = 11)
    private String patientTc; // TC Kimlik No

    @Column(name = "patient_name", nullable = false, length = 200)
    private String patientName; // Hasta adı soyadı (cache için)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policlinic_id")
    private Policlinic policlinic; // Poliklinik

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate; // Randevu tarihi

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime; // Randevu saati (15 dakikalık slotlar)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AppointmentStatus status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // Hasta notları

    @Column(name = "doctor_notes", columnDefinition = "TEXT")
    private String doctorNotes; // Doktor notları

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason; // İptal nedeni

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt; // İptal zamanı

    @Column(name = "cancelled_by", length = 50)
    private String cancelledBy; // Kim iptal etti (PATIENT/DOCTOR/SYSTEM)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "reminder_sent")
    private Boolean reminderSent = false; // Hatırlatma gönderildi mi?

    @Column(name = "patient_arrived")
    private Boolean patientArrived = false; // Hasta geldi mi?

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime; // Hasta geliş zamanı

    // Muayene süresi (dakika)
    @Column(name = "examination_duration")
    private Integer examinationDuration = 15; // Default 15 dakika

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Appointment() {}

    public Appointment(Long patientId, String patientTc, String patientName, 
                      Doctor doctor, Hospital hospital, 
                      LocalDate appointmentDate, LocalTime appointmentTime) {
        this.patientId = patientId;
        this.patientTc = patientTc;
        this.patientName = patientName;
        this.doctor = doctor;
        this.hospital = hospital;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = AppointmentStatus.SCHEDULED;
        this.appointmentCode = generateAppointmentCode();
    }

    // Randevu kodu üretme
    private String generateAppointmentCode() {
        return "RND" + System.currentTimeMillis();
    }

    // Randevunun tam zamanını döndür
    public LocalDateTime getAppointmentDateTime() {
        return LocalDateTime.of(appointmentDate, appointmentTime);
    }

    // Randevu geçmiş mi kontrolü
    public boolean isPast() {
        return getAppointmentDateTime().isBefore(LocalDateTime.now());
    }

    // Randevu iptal edilebilir mi? (En az 30 dakika önce)
    public boolean isCancellable() {
        return !isPast() && 
               status == AppointmentStatus.SCHEDULED &&
               getAppointmentDateTime().minusMinutes(30).isAfter(LocalDateTime.now());
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAppointmentCode() { return appointmentCode; }
    public void setAppointmentCode(String appointmentCode) { this.appointmentCode = appointmentCode; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientTc() { return patientTc; }
    public void setPatientTc(String patientTc) { this.patientTc = patientTc; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }

    public Policlinic getPoliclinic() { return policlinic; }
    public void setPoliclinic(Policlinic policlinic) { this.policlinic = policlinic; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getDoctorNotes() { return doctorNotes; }
    public void setDoctorNotes(String doctorNotes) { this.doctorNotes = doctorNotes; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(String cancelledBy) { this.cancelledBy = cancelledBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getReminderSent() { return reminderSent; }
    public void setReminderSent(Boolean reminderSent) { this.reminderSent = reminderSent; }

    public Boolean getPatientArrived() { return patientArrived; }
    public void setPatientArrived(Boolean patientArrived) { this.patientArrived = patientArrived; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public Integer getExaminationDuration() { return examinationDuration; }
    public void setExaminationDuration(Integer examinationDuration) { this.examinationDuration = examinationDuration; }
}
