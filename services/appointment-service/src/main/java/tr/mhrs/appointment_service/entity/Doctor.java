package tr.mhrs.appointment_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "doctors")
public class Doctor {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(name = "tc_no", length = 11, unique = true)
    private String tcNo; // TC Kimlik No

    @Column(name = "diploma_no", length = 50, unique = true)
    private String diplomaNo; // Diploma numarası

    @Column(name = "title", length = 50)
    private String title; // Dr., Prof. Dr., Doç. Dr., Uzm. Dr.

    @Column(name = "license_number", length = 50, unique = true)
    private String licenseNumber; // Doktor lisans numarası

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience; // Kaç yıl deneyim

    @Column(name = "birth_date")
    private java.time.LocalDate birthDate; // Doğum tarihi

    @Enumerated(EnumType.STRING)
    @Column(name = "doctor_status")
    private DoctorStatus doctorStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private DoctorStatus status; // Repository'de status kullanılıyor

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // İlişki: Bir doktor bir hastanede çalışır
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    // İlişki: Bir doktorun bir uzmanlığı vardır
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Speciality speciality;

    // İlişki: Bir doktor birden fazla poliklinikte çalışabilir
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "doctor_policlinics",
        joinColumns = @JoinColumn(name = "doctor_id"),
        inverseJoinColumns = @JoinColumn(name = "policlinic_id")
    )
    private Set<Policlinic> policlinics = new HashSet<>();

    // İlişki: Bir doktorun birçok randevusu olur
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();
    
    // İlişki: Bir doktorun çalışma takvimleri
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DoctorSchedule> schedules = new ArrayList<>();

    // Constructors
    public Doctor() {}

    public Doctor(String firstName, String lastName, String licenseNumber,
                  Hospital hospital, Speciality specialty) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.licenseNumber = licenseNumber;
        this.hospital = hospital;
        this.speciality = speciality;
        this.doctorStatus = DoctorStatus.ACTIVE; // Default aktif
        this.status = DoctorStatus.ACTIVE; // Default aktif
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTcNo() { return tcNo; }
    public void setTcNo(String tcNo) { this.tcNo = tcNo; }

    public String getDiplomaNo() { return diplomaNo; }
    public void setDiplomaNo(String diplomaNo) { this.diplomaNo = diplomaNo; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public java.time.LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(java.time.LocalDate birthDate) { this.birthDate = birthDate; }

    public DoctorStatus getDoctorStatus() { return doctorStatus; }
    public void setDoctorStatus(DoctorStatus doctorStatus) { this.doctorStatus = doctorStatus; }

    public DoctorStatus getStatus() { return status; }
    public void setStatus(DoctorStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }

    public Speciality getSpeciality() { return speciality; }
    public void setSpeciality(Speciality speciality) { this.speciality = speciality; }

    public Set<Policlinic> getPoliclinics() { return policlinics; }
    public void setPoliclinics(Set<Policlinic> policlinics) { this.policlinics = policlinics; }

    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }

    public List<DoctorSchedule> getSchedules() { return schedules; }
    public void setSchedules(List<DoctorSchedule> schedules) { this.schedules = schedules; }

    // Convenience method - Doktorun tam adı
    public String getFullName() {
        return firstName + " " + lastName;
    }
}