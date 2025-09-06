package tr.mhrs.appointment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tr.mhrs.appointment_service.entity.Policlinic;

import java.util.List;

@Repository
public interface PoliclinicRepository extends JpaRepository<Policlinic, Long> {
    
    // Hastaneye göre poliklinikleri bul
    List<Policlinic> findByHospitalId(Long hospitalId);
    
    // Uzmanlığa göre poliklinikleri bul
    List<Policlinic> findBySpecialityId(Long specialityId);
    
    // Hastane ve uzmanlığa göre poliklinikleri bul
    List<Policlinic> findByHospitalIdAndSpecialityId(Long hospitalId, Long specialityId);
    
    // Aktif poliklinikleri bul
    List<Policlinic> findByIsActive(Boolean isActive);
    
    // Kod ile poliklinik bul
    Policlinic findByCode(String code);
    
    // Hastanedeki aktif poliklinikleri bul
    @Query("SELECT p FROM Policlinic p WHERE p.hospital.id = :hospitalId AND p.isActive = true")
    List<Policlinic> findActiveByHospitalId(@Param("hospitalId") Long hospitalId);
}