package tr.mhrs.contracts.events;

public record AppointmentCancelledEvent(
        Long appointmentId,
        Long patientId,
        Long doctorId,
        Long clinicId,
        String reason
) {}
