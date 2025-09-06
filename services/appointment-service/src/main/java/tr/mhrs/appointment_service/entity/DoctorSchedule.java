package tr.mhrs.appointment_service.entity;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "doctor_schedules",
       indexes = {
           @Index(name = "idx_doctor_schedule", columnList = "doctor_id, schedule_date"),
           @Index(name = "idx_schedule_date", columnList = "schedule_date")
       })
public class DoctorSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policlinic_id")
    private Policlinic policlinic;
    
    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate; // Çalışma tarihi
    
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek; // Haftanın günü
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // Başlangıç saati (örn: 09:00)
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime; // Bitiş saati (örn: 17:00)
    
    @Column(name = "lunch_break_start")
    private LocalTime lunchBreakStart; // Öğle arası başlangıç (örn: 12:00)
    
    @Column(name = "lunch_break_end")
    private LocalTime lunchBreakEnd; // Öğle arası bitiş (örn: 13:00)
    
    @Column(name = "slot_duration_minutes")
    private Integer slotDurationMinutes = 15; // Her randevu slot süresi (dakika)
    
    @Column(name = "max_appointments")
    private Integer maxAppointments; // Bu gün için maksimum randevu sayısı
    
    @Column(name = "booked_appointments")
    private Integer bookedAppointments = 0; // Rezerve edilmiş randevu sayısı
    
    @Column(name = "is_available")
    private Boolean isAvailable = true; // Doktor bu gün müsait mi?
    
    @Column(name = "is_holiday")
    private Boolean isHoliday = false; // Tatil günü mü?
    
    @Column(name = "unavailable_reason", length = 200)
    private String unavailableReason; // Müsait değilse nedeni (izin, kongre vs.)
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        if (dayOfWeek == null && scheduleDate != null) {
            dayOfWeek = scheduleDate.getDayOfWeek();
        }
        calculateMaxAppointments();
    }
    
    // Maksimum randevu sayısını hesapla
    public void calculateMaxAppointments() {
        if (startTime != null && endTime != null && slotDurationMinutes != null) {
            int totalMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
            
            // Öğle arasını çıkar
            if (lunchBreakStart != null && lunchBreakEnd != null) {
                int lunchMinutes = (int) java.time.Duration.between(lunchBreakStart, lunchBreakEnd).toMinutes();
                totalMinutes -= lunchMinutes;
            }
            
            this.maxAppointments = totalMinutes / slotDurationMinutes;
        }
    }
    
    // Müsait slot sayısını döndür
    public Integer getAvailableSlots() {
        if (maxAppointments == null || bookedAppointments == null) {
            return 0;
        }
        return maxAppointments - bookedAppointments;
    }
    
    // Randevu alınabilir mi?
    public boolean canBookAppointment() {
        return isAvailable && !isHoliday && getAvailableSlots() > 0;
    }
    
    // Constructors
    public DoctorSchedule() {}
    
    public DoctorSchedule(Doctor doctor, LocalDate scheduleDate, 
                         LocalTime startTime, LocalTime endTime) {
        this.doctor = doctor;
        this.scheduleDate = scheduleDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = scheduleDate.getDayOfWeek();
        calculateMaxAppointments();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    
    public Policlinic getPoliclinic() { return policlinic; }
    public void setPoliclinic(Policlinic policlinic) { this.policlinic = policlinic; }
    
    public LocalDate getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(LocalDate scheduleDate) { 
        this.scheduleDate = scheduleDate;
        if (scheduleDate != null) {
            this.dayOfWeek = scheduleDate.getDayOfWeek();
        }
    }
    
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { 
        this.startTime = startTime;
        calculateMaxAppointments();
    }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { 
        this.endTime = endTime;
        calculateMaxAppointments();
    }
    
    public LocalTime getLunchBreakStart() { return lunchBreakStart; }
    public void setLunchBreakStart(LocalTime lunchBreakStart) { 
        this.lunchBreakStart = lunchBreakStart;
        calculateMaxAppointments();
    }
    
    public LocalTime getLunchBreakEnd() { return lunchBreakEnd; }
    public void setLunchBreakEnd(LocalTime lunchBreakEnd) { 
        this.lunchBreakEnd = lunchBreakEnd;
        calculateMaxAppointments();
    }
    
    public Integer getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(Integer slotDurationMinutes) { 
        this.slotDurationMinutes = slotDurationMinutes;
        calculateMaxAppointments();
    }
    
    public Integer getMaxAppointments() { return maxAppointments; }
    public void setMaxAppointments(Integer maxAppointments) { this.maxAppointments = maxAppointments; }
    
    public Integer getBookedAppointments() { return bookedAppointments; }
    public void setBookedAppointments(Integer bookedAppointments) { this.bookedAppointments = bookedAppointments; }
    
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    
    public Boolean getIsHoliday() { return isHoliday; }
    public void setIsHoliday(Boolean isHoliday) { this.isHoliday = isHoliday; }
    
    public String getUnavailableReason() { return unavailableReason; }
    public void setUnavailableReason(String unavailableReason) { this.unavailableReason = unavailableReason; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
