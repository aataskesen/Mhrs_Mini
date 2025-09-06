package tr.mhrs.contracts.dto.schedule;

import java.time.LocalDateTime;

public record SlotResponse(
        Long id,
        Long doctorId,
        Long clinicId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        boolean isAvailable
) {}
