package tr.mhrs.appointment_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.mhrs.appointment_service.entity.Doctor;
import tr.mhrs.appointment_service.service.DoctorService;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService doctorService;
    
    // Manuel constructor (Lombok çalışmıyorsa)
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // HASTANEDEKİ DOKTORLARI LİSTELE
    @GetMapping("/by-hospital/{hospitalId}")
    public ResponseEntity<List<Doctor>> getDoctorsByHospital(@PathVariable Long hospitalId) {
        List<Doctor> doctors = doctorService.findDoctorsByHospital(hospitalId);
        return ResponseEntity.ok(doctors);
    }

    // UZMANLIĞA GÖRE DOKTORLARI LİSTELE
    @GetMapping("/by-speciality/{specialityId}")
    public ResponseEntity<List<Doctor>> getDoctorsBySpeciality(@PathVariable Long specialityId) {
        List<Doctor> doctors = doctorService.findDoctorsBySpeciality(specialityId);
        return ResponseEntity.ok(doctors);
    }

    // İSİMLE DOKTOR ARA
    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> searchDoctors(@RequestParam String name) {
        List<Doctor> doctors = doctorService.searchDoctors(name);
        return ResponseEntity.ok(doctors);
    }

    // DOKTOR DETAYI
    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorDetail(@PathVariable Long id) {
        try {
            Doctor doctor = doctorService.getDoctorDetail(id);
            return ResponseEntity.ok(doctor);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // HASTANE VE UZMANLIĞA GÖRE
    @GetMapping("/filter")
    public ResponseEntity<List<Doctor>> filterDoctors(
            @RequestParam Long hospitalId,
            @RequestParam Long specialityId) {
        List<Doctor> doctors = doctorService.findDoctorsByHospitalAndSpeciality(hospitalId, specialityId);
        return ResponseEntity.ok(doctors);
    }

    // TÜM AKTİF DOKTORLAR
    @GetMapping("/active")
    public ResponseEntity<List<Doctor>> getActiveDoctors() {
        List<Doctor> doctors = doctorService.getAllActiveDoctors();
        return ResponseEntity.ok(doctors);
    }

    // TEST ENDPOINT
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Doctor Service Çalışıyor!");
    }
}