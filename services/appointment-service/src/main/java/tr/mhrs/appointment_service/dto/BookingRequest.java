package tr.mhrs.appointment_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Randevu alma isteği için DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    
    @NotBlank(message = "Hasta TC kimlik numarası boş olamaz")
    @Size(min = 11, max = 11, message = "TC kimlik numarası 11 haneli olmalıdır")
    private String patientTc;
    
    @NotNull(message = "Hasta ID boş olamaz")
    private Long patientId;
    
    @NotBlank(message = "Hasta adı boş olamaz")
    private String patientName;
    
    @NotNull(message = "Doktor seçimi zorunludur")
    private Long doctorId;
    
    @NotNull(message = "Hastane seçimi zorunludur")
    private Long hospitalId;
    
    private Long policlinicId;  // Poliklinik ID (opsiyonel)
    
    @NotNull(message = "Randevu tarihi boş olamaz")
    @Future(message = "Randevu tarihi ileri bir tarih olmalıdır")
    private LocalDate appointmentDate;
    
    @NotNull(message = "Randevu saati boş olamaz")
    private LocalTime appointmentTime;
    
    private String notes;  // Hasta notları (opsiyonel)
    
    // Lombok @Data annotation'ı sayesinde getter/setter'lar otomatik oluşturulur
    // toString, equals, hashCode metodları da otomatik oluşur
}
