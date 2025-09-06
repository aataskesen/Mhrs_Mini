package tr.mhrs.appointment_service.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "time_slots",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"doctor_id", "slot_date", "slot_time"})
       },
       indexes = {
           @Index(name = "idx_slot_datetime", columnList = "slot_date, slot_time"),
           @Index(name = "idx_doctor_slot", columnList = "doctor_id, slot_date"),
           @Index(name = "idx_slot_status", columnList = "status")
       })
public class TimeSlot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_schedule_id", nullable = false)
    private DoctorSchedule doctorSchedule;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policlinic_id")
    private Policlinic policlinic;
    
    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate; // Slot tarihi
    
    @Column(name = "slot_time", nullable = false)
    private LocalTime slotTime; // Slot saati (örn: 09:00, 09:15, 09:30...)
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SlotStatus status = SlotStatus.AVAILABLE;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment; // Bu slota ait randevu (eğer rezerve edilmişse)
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes = 15; // Slot süresi (dakika)
    
    @Column(name = "is_online_bookable")
    private Boolean isOnlineBookable = true; // Online randevu alınabilir mi?
    
    @Column(name = "is_urgent_slot")
    private Boolean isUrgentSlot = false; // Acil randevu slotu mu?
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "reserved_at")
    private LocalDateTime reservedAt; // Rezerve edilme zamanı
    
    @Column(name = "reserved_until")
    private LocalDateTime reservedUntil; // Rezervasyon timeout süresi (5 dakika)
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Slot'un tam zamanını döndür
    public LocalDateTime getSlotDateTime() {
        return LocalDateTime.of(slotDate, slotTime);
    }
    
    // Slot müsait mi?
    public boolean isAvailable() {
        // Rezervasyon timeout kontrolü
        if (status == SlotStatus.RESERVED && reservedUntil != null) {
            if (LocalDateTime.now().isAfter(reservedUntil)) {
                // Rezervasyon süresi dolmuş, slotu tekrar müsait yap
                this.status = SlotStatus.AVAILABLE;
                this.reservedAt = null;
                this.reservedUntil = null;
                return true;
            }
        }
        return status == SlotStatus.AVAILABLE && isOnlineBookable;
    }
    
    // Slot'u rezerve et (5 dakikalık timeout ile)
    public void reserve() {
        this.status = SlotStatus.RESERVED;
        this.reservedAt = LocalDateTime.now();
        this.reservedUntil = LocalDateTime.now().plusMinutes(5);
    }
    
    // Slot'u kesin olarak ayır
    public void book(Appointment appointment) {
        this.status = SlotStatus.BOOKED;
        this.appointment = appointment;
        this.reservedAt = null;
        this.reservedUntil = null;
    }
    
    // Slot'u serbest bırak
    public void release() {
        this.status = SlotStatus.AVAILABLE;
        this.appointment = null;
        this.reservedAt = null;
        this.reservedUntil = null;
    }
    
    // Slot geçmiş mi?
    public boolean isPast() {
        return getSlotDateTime().isBefore(LocalDateTime.now());
    }
    
    // Constructors
    public TimeSlot() {}
    
    public TimeSlot(Doctor doctor, DoctorSchedule schedule, 
                   LocalDate date, LocalTime time) {
        this.doctor = doctor;
        this.doctorSchedule = schedule;
        this.slotDate = date;
        this.slotTime = time;
        this.policlinic = schedule.getPoliclinic();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    
    public DoctorSchedule getDoctorSchedule() { return doctorSchedule; }
    public void setDoctorSchedule(DoctorSchedule doctorSchedule) { this.doctorSchedule = doctorSchedule; }
    
    public Policlinic getPoliclinic() { return policlinic; }
    public void setPoliclinic(Policlinic policlinic) { this.policlinic = policlinic; }
    
    public LocalDate getSlotDate() { return slotDate; }
    public void setSlotDate(LocalDate slotDate) { this.slotDate = slotDate; }
    
    public LocalTime getSlotTime() { return slotTime; }
    public void setSlotTime(LocalTime slotTime) { this.slotTime = slotTime; }
    
    public SlotStatus getStatus() { return status; }
    public void setStatus(SlotStatus status) { this.status = status; }
    
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    
    public Boolean getIsOnlineBookable() { return isOnlineBookable; }
    public void setIsOnlineBookable(Boolean isOnlineBookable) { this.isOnlineBookable = isOnlineBookable; }
    
    public Boolean getIsUrgentSlot() { return isUrgentSlot; }
    public void setIsUrgentSlot(Boolean isUrgentSlot) { this.isUrgentSlot = isUrgentSlot; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getReservedAt() { return reservedAt; }
    public void setReservedAt(LocalDateTime reservedAt) { this.reservedAt = reservedAt; }
    
    public LocalDateTime getReservedUntil() { return reservedUntil; }
    public void setReservedUntil(LocalDateTime reservedUntil) { this.reservedUntil = reservedUntil; }
}
