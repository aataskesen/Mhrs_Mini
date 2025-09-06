package tr.mhrs.appointment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Randevu işlem sonucu için DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {
    
    private boolean success;
    private String message;
    private String appointmentCode;
    private String patientName;
    private String patientTc;
    private String doctorName;
    private String hospitalName;
    private String policlinicName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;
    
    // Başarılı randevu için statik factory method
    public static AppointmentResponse success(String appointmentCode, String message) {
        return AppointmentResponse.builder()
                .success(true)
                .appointmentCode(appointmentCode)
                .message(message)
                .status("SCHEDULED")
                .build();
    }
    
    // Hata durumu için statik factory method
    public static AppointmentResponse error(String message) {
        return AppointmentResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
