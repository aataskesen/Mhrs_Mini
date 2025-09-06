package tr.mhrs.appointment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.mhrs.appointment_service.entity.Speciality;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialityRepository extends JpaRepository<Speciality, Long> {
    
    // İsimle uzmanlık bul
    Optional<Speciality> findByName(String name);
    
    // İsim içeren uzmanlıkları bul
    List<Speciality> findByNameContainingIgnoreCase(String name);
    
    // Açıklama içeren uzmanlıkları bul
    List<Speciality> findByDescriptionContainingIgnoreCase(String description);
}