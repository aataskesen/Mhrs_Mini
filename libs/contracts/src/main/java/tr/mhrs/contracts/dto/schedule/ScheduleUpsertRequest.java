package tr.mhrs.contracts.dto.schedule;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record ScheduleUpsertRequest(
        @NotNull Long doctorId,
        @NotNull Long clinicId,
        @NotNull LocalDateTime startAt,
        @NotNull @Positive int slotMinutes, // 10, 15, 20...
        @NotNull @Positive int slotCount    // kaç slot üretilecek
) {}
