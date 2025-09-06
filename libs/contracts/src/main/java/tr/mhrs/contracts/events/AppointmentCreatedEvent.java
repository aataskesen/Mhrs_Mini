package tr.mhrs.contracts.events;

import java.time.LocalDateTime;

public record AppointmentCreatedEvent(
        Long appointmentId,
        Long patientId,
        String patientEmail,
        String patientPhone,
        Long doctorId,
        Long clinicId,
        LocalDateTime startAt
) {}
