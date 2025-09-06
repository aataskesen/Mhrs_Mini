package tr.mhrs.contracts.dto.appointment;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record AppointmentCreateRequest(
        @NotNull Long patientId,
        @NotNull Long doctorId,
        @NotNull Long clinicId,
        @NotNull @FutureOrPresent LocalDateTime startAt
) {}
