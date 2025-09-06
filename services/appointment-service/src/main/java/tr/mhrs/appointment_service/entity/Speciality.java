package tr.mhrs.appointment_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "specialties")
public class Speciality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name; // "Dahiliye", "Kardiyoloji", "Ortopedi"

    @Column(length = 500)
    private String description; // "İç hastalıkları uzmanı"

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // İlişki: Bir uzmanlıkta birçok doktor çalışır
     @OneToMany(mappedBy = "speciality", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
     private List<Doctor> doctors; // Doctor entity'deki speciality field'ına referans

    // Constructors
    public Speciality() {}

    public Speciality(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Doctor ilişkisi için getter/setter (sonra ekleyeceğiz)
     public List<Doctor> getDoctors() { return doctors; }
     public void setDoctors(List<Doctor> doctors) { this.doctors = doctors; }
}