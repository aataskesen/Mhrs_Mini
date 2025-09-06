package tr.mhrs.appointment_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "hospitals")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String address;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "hospital_type")
    private HospitalType hospitalType;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    //Bir ilçede bir hastane bulunur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = true)  // Geçici olarak nullable
    private District district;

    //Bir şehirde hastane bulunur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = true)  // Geçici olarak nullable
    private City city;


// City field'ından sonra bu ilişkileri ekleyin:

    // Bir hastanede birden çok poliklinik vardır
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Policlinic> policlinics = new ArrayList<>();

    // Bir hastanede birden çok doktor çalışır
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Doctor> doctors = new ArrayList<>();


    //Constructors
    public Hospital() {}

    public Hospital(String name, String address, String phone, HospitalType hospitalType, District district, City city) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.hospitalType = hospitalType;
        this.district = district;
        this.city = city;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}

    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}

    public HospitalType getHospitalType() {return hospitalType;}
    public void setHospitalType(HospitalType hospitalType) {this.hospitalType = hospitalType;}

    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}
    public void setUpdatedAt(LocalDateTime updatedAt) {this.updatedAt = updatedAt;}

    public District getDistrict() {return district;}
    public void setDistrict(District district) {this.district = district;}

    public City getCity() {return city;}
    public void setCity(City city) {this.city = city;}

    public List<Policlinic> getPoliclinics() { return policlinics; }
    public void setPoliclinics(List<Policlinic> policlinics) { this.policlinics = policlinics; }

    public List<Doctor> getDoctors() { return doctors; }
    public void setDoctors(List<Doctor> doctors) { this.doctors = doctors; }

}