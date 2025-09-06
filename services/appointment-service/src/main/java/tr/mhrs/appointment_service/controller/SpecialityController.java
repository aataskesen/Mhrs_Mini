package tr.mhrs.appointment_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.mhrs.appointment_service.entity.Speciality;
import tr.mhrs.appointment_service.repository.SpecialityRepository;

import java.util.List;

@RestController
@RequestMapping("/api/specialities")
@CrossOrigin(origins = "*")
public class SpecialityController {

    private final SpecialityRepository specialityRepository;

    public SpecialityController(SpecialityRepository specialityRepository) {
        this.specialityRepository = specialityRepository;
    }

    // TEST ENDPOINT
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Speciality Service Çalışıyor!");
    }

    // TÜM UZMANLIK ALANLARINI LİSTELE
    @GetMapping
    public ResponseEntity<List<Speciality>> getAllSpecialities() {
        List<Speciality> specialities = specialityRepository.findAll();
        return ResponseEntity.ok(specialities);
    }

    // ID İLE UZMANLIK BUL
    @GetMapping("/{id}")
    public ResponseEntity<Speciality> getSpecialityById(@PathVariable Long id) {
        return specialityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // İSİMLE UZMANLIK BUL
    @GetMapping("/search")
    public ResponseEntity<List<Speciality>> searchByName(@RequestParam String name) {
        List<Speciality> specialities = specialityRepository.findByNameContainingIgnoreCase(name);
        return ResponseEntity.ok(specialities);
    }
}