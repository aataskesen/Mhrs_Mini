package tr.mhrs.contracts.dto.doctor;

public record DoctorResponse(
        Long id,
        String firstName,
        String lastName,
        String branch,    // uzmanlık
        Long clinicId
) {}
