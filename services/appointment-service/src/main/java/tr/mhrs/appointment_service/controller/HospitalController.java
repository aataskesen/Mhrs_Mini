package tr.mhrs.appointment_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.mhrs.appointment_service.entity.Hospital;
import tr.mhrs.appointment_service.repository.HospitalRepository;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
@CrossOrigin(origins = "*")
public class HospitalController {

    private final HospitalRepository hospitalRepository;

    public HospitalController(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    // TEST ENDPOINT
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hospital Service Çalışıyor!");
    }

    // TÜM HASTANELERİ LİSTELE
    @GetMapping
    public ResponseEntity<List<Hospital>> getAllHospitals() {
        List<Hospital> hospitals = hospitalRepository.findAll();
        return ResponseEntity.ok(hospitals);
    }

    // ID İLE HASTANE BUL
    @GetMapping("/{id}")
    public ResponseEntity<Hospital> getHospitalById(@PathVariable Long id) {
        return hospitalRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}