package tr.mhrs.appointment_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "policlinics")
public class Policlinic {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name; // Poliklinik adı (örn: "Göz Polikliniği", "Dahiliye Polikliniği")
    
    @Column(length = 10)
    private String code; // Poliklinik kodu
    
    @Column(name = "floor_number")
    private Integer floorNumber; // Kat numarası
    
    @Column(name = "room_number", length = 20)
    private String roomNumber; // Oda numarası
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "max_daily_appointments")
    private Integer maxDailyAppointments = 40; // Günlük maksimum randevu sayısı
    
    @Column(name = "appointment_duration")
    private Integer appointmentDuration = 15; // Randevu süresi (dakika)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "speciality_id", nullable = false)
    private Speciality speciality; // Hangi uzmanlık alanı
    
    // Bir poliklinikte birden fazla doktor çalışabilir
    @ManyToMany(mappedBy = "policlinics", fetch = FetchType.LAZY)
    private Set<Doctor> doctors = new HashSet<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Policlinic() {}
    
    public Policlinic(String name, String code, Hospital hospital, Speciality speciality) {
        this.name = name;
        this.code = code;
        this.hospital = hospital;
        this.speciality = speciality;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public Integer getFloorNumber() { return floorNumber; }
    public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }
    
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getMaxDailyAppointments() { return maxDailyAppointments; }
    public void setMaxDailyAppointments(Integer maxDailyAppointments) { this.maxDailyAppointments = maxDailyAppointments; }
    
    public Integer getAppointmentDuration() { return appointmentDuration; }
    public void setAppointmentDuration(Integer appointmentDuration) { this.appointmentDuration = appointmentDuration; }
    
    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }
    
    public Speciality getSpeciality() { return speciality; }
    public void setSpeciality(Speciality speciality) { this.speciality = speciality; }
    
    public Set<Doctor> getDoctors() { return doctors; }
    public void setDoctors(Set<Doctor> doctors) { this.doctors = doctors; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
