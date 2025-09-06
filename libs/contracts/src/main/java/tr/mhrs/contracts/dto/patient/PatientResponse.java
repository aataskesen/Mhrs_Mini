package tr.mhrs.contracts.dto.patient;

import java.time.LocalDate;

public record PatientResponse(
        Long id,
        String tckn,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String email,
        String phone
) {}
