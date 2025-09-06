package tr.mhrs.contracts.dto.patient;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record PatientCreateRequest(
        @NotBlank @Pattern(regexp="\\d{11}", message="TCKN 11 haneli olmalı") String tckn,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull @Past LocalDate birthDate,
        @Email String email,
        @NotBlank String phone
) {}
