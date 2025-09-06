package tr.mhrs.appointment_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tr.mhrs.appointment_service.entity.*;
import tr.mhrs.appointment_service.repository.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            HospitalRepository hospitalRepository,
            DoctorRepository doctorRepository,
            SpecialityRepository specialityRepository,
            PoliclinicRepository policlinicRepository) {
        
        return args -> {
            // Eğer veri varsa ekleme
            if (specialityRepository.count() > 0) {
                return;
            }
            
            System.out.println("📊 Test verileri oluşturuluyor...");
            
            // 1. UZMANLIK ALANLARI
            Speciality dahiliye = new Speciality("Dahiliye", "İç Hastalıkları");
            Speciality kardiyoloji = new Speciality("Kardiyoloji", "Kalp ve Damar Hastalıkları");
            Speciality ortopedi = new Speciality("Ortopedi", "Kemik ve Kas Hastalıkları");
            Speciality cocukSagligi = new Speciality("Çocuk Sağlığı", "Pediatri");
            Speciality goz = new Speciality("Göz Hastalıkları", "Oftalmoloji");
            Speciality kbb = new Speciality("Kulak Burun Boğaz", "KBB ve Baş Boyun Cerrahisi");
            Speciality noroloji = new Speciality("Nöroloji", "Sinir Sistemi Hastalıkları");
            Speciality psikiyatri = new Speciality("Psikiyatri", "Ruh Sağlığı ve Hastalıkları");
            
            List<Speciality> specialities = Arrays.asList(
                dahiliye, kardiyoloji, ortopedi, cocukSagligi, 
                goz, kbb, noroloji, psikiyatri
            );
            specialityRepository.saveAll(specialities);
            System.out.println("✅ " + specialities.size() + " uzmanlık alanı eklendi");
            
            // 2. HASTANELER
            // Not: City ve District entity'leri henüz yok, geçici olarak null bırakıyoruz
            Hospital pendikDevlet = new Hospital();
            pendikDevlet.setName("Pendik Devlet Hastanesi");
            pendikDevlet.setHospitalType(HospitalType.PUBLIC);
            pendikDevlet.setAddress("Pendik, İstanbul");
            pendikDevlet.setPhone("02166661111");
            // pendikDevlet.setCity(null);  // TODO: City entity eklenince düzeltilecek
            // pendikDevlet.setDistrict(null);  // TODO: District entity eklenince düzeltilecek
            
            Hospital kartalEgitim = new Hospital();
            kartalEgitim.setName("Kartal Dr. Lütfi Kırdar Eğitim ve Araştırma Hastanesi");
            kartalEgitim.setHospitalType(HospitalType.PUBLIC);
            kartalEgitim.setAddress("Kartal, İstanbul");
            kartalEgitim.setPhone("02164413900");
            // kartalEgitim.setCity(null);
            // kartalEgitim.setDistrict(null);
            
            Hospital medicalPark = new Hospital();
            medicalPark.setName("Medical Park Pendik Hastanesi");
            medicalPark.setHospitalType(HospitalType.PRIVATE);
            medicalPark.setAddress("Pendik, İstanbul");
            medicalPark.setPhone("02166570000");
            // medicalPark.setCity(null);
            // medicalPark.setDistrict(null);
            
            List<Hospital> hospitals = Arrays.asList(pendikDevlet, kartalEgitim, medicalPark);
            hospitalRepository.saveAll(hospitals);
            System.out.println("✅ " + hospitals.size() + " hastane eklendi");
            
            // 3. DOKTORLAR
            createDoctor("12345678901", "Ahmet", "Yılmaz", "Dr.", 
                dahiliye, pendikDevlet, doctorRepository);
            
            createDoctor("12345678902", "Mehmet", "Öztürk", "Doç. Dr.", 
                kardiyoloji, pendikDevlet, doctorRepository);
            
            createDoctor("12345678903", "Ayşe", "Kaya", "Prof. Dr.", 
                kardiyoloji, kartalEgitim, doctorRepository);
            
            createDoctor("12345678904", "Fatma", "Demir", "Uzm. Dr.", 
                ortopedi, kartalEgitim, doctorRepository);
            
            createDoctor("12345678905", "Ali", "Çelik", "Dr.", 
                cocukSagligi, medicalPark, doctorRepository);
            
            createDoctor("12345678906", "Zeynep", "Arslan", "Uzm. Dr.", 
                goz, medicalPark, doctorRepository);
            
            createDoctor("12345678907", "Mustafa", "Şahin", "Dr.", 
                kbb, pendikDevlet, doctorRepository);
            
            createDoctor("12345678908", "Elif", "Yıldız", "Prof. Dr.", 
                noroloji, kartalEgitim, doctorRepository);
            
            System.out.println("✅ 8 doktor eklendi");
            
            // 4. POLİKLİNİKLER
            createPoliclinic("DAH-01", "Dahiliye Polikliniği", 
                dahiliye, pendikDevlet, 2, "201", policlinicRepository);
            
            createPoliclinic("KRD-01", "Kardiyoloji Polikliniği", 
                kardiyoloji, pendikDevlet, 2, "205", policlinicRepository);
            
            createPoliclinic("KRD-02", "Kardiyoloji Polikliniği", 
                kardiyoloji, kartalEgitim, 3, "301", policlinicRepository);
            
            createPoliclinic("ORT-01", "Ortopedi Polikliniği", 
                ortopedi, kartalEgitim, 1, "105", policlinicRepository);
            
            createPoliclinic("COC-01", "Çocuk Hastalıkları", 
                cocukSagligi, medicalPark, 4, "401", policlinicRepository);
            
            System.out.println("✅ 5 poliklinik eklendi");
            
            System.out.println("🎉 Test verileri başarıyla oluşturuldu!");
        };
    }
    
    private void createDoctor(String tcNo, String firstName, String lastName, 
                             String title, Speciality speciality, Hospital hospital,
                             DoctorRepository repository) {
        Doctor doctor = new Doctor();
        doctor.setTcNo(tcNo);
        doctor.setFirstName(firstName);
        doctor.setLastName(lastName);
        doctor.setTitle(title);
        doctor.setSpeciality(speciality);
        doctor.setHospital(hospital);
        doctor.setStatus(DoctorStatus.ACTIVE);
        doctor.setDoctorStatus(DoctorStatus.ACTIVE);
        doctor.setEmail(firstName.toLowerCase() + "@hospital.com");
        doctor.setPhone("05551234567");
        doctor.setYearsOfExperience(10);
        // Her doktora farklı license ve diploma no verelim
        doctor.setLicenseNumber("LIC-" + tcNo);  // Tüm TC'yi kullan
        doctor.setDiplomaNo("DIP-" + tcNo);      // Tüm TC'yi kullan
        repository.save(doctor);
    }
    
    private void createPoliclinic(String code, String name, Speciality speciality,
                                  Hospital hospital, int floor, String roomNumber,
                                  PoliclinicRepository repository) {
        Policlinic policlinic = new Policlinic();
        policlinic.setCode(code);
        policlinic.setName(name);
        policlinic.setSpeciality(speciality);
        policlinic.setHospital(hospital);
        policlinic.setFloorNumber(floor);
        policlinic.setRoomNumber(roomNumber);
        policlinic.setIsActive(true);
        policlinic.setMaxDailyAppointments(40);
        policlinic.setAppointmentDuration(15);
        repository.save(policlinic);
    }
}