package tr.mhrs.contracts.dto.appointment;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        Long doctorId,
        Long clinicId,
        LocalDateTime startAt,
        String status   // CREATED / CANCELLED
) {}
