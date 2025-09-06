package tr.mhrs.auth.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cities")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "plate_code", nullable = false, unique = true)
    private Integer plateCode;

    @Column(name = "region", length = 50)
    private String region; // Bölge (Marmara, Ege, vb.)

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<District> districts = new ArrayList<>();

    // Constructors
    public City() {}

    public City(String name, Integer plateCode, String region) {
        this.name = name;
        this.plateCode = plateCode;
        this.region = region;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPlateCode() { return plateCode; }
    public void setPlateCode(Integer plateCode) { this.plateCode = plateCode; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public List<District> getDistricts() { return districts; }
    public void setDistricts(List<District> districts) { this.districts = districts; }
}
