package tr.mhrs.contracts.dto.clinic;

public record ClinicResponse(
        Long id,
        String name,
        String city,
        String district,
        String address
) {}
