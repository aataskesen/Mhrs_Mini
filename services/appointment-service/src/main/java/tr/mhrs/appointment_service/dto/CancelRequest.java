package tr.mhrs.appointment_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Randevu iptal isteği için DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelRequest {
    
    @NotBlank(message = "Randevu kodu boş olamaz")
    private String appointmentCode;
    
    private String cancellationReason;  // İptal nedeni (opsiyonel)
    
    private String cancelledBy;  // Kim iptal etti (PATIENT/DOCTOR/SYSTEM)
}
