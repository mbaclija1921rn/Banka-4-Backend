package rs.banka4.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Set;

@Schema(description = "DTO for returned clients in response")
public record ClientDto(
        @Schema(description = "Client's id", example = "1de54a3a-d879-4154-aa3a-e40598186f93")
        String id,
        @Schema(description = "Client's first name", example = "Djovak")
        String firstName,
        @Schema(description = "Client's last name", example = "Nokovic")
        String lastName,
        @Schema(description = "Client's date of birth", example = "1990-05-15")
        LocalDate dateOfBirth,
        @Schema(description = "Client's gender", example = "Male")
        String gender,
        @Schema(description = "Client's email address", example = "djovaknokovic@example.com")
        String email,
        @Schema(description = "Client's phone number", example = "+1234567890")
        String phone,
        @Schema(description = "Client's address", example = "123 Grove Street, City, Country")
        String address,
        @Schema(description = "Indicates if the client is active", example = "true")
        boolean enabled,
        @Schema(description = "Client's linked accounts", example = "[\"265000000000123456\"]")
        Set<String> linkedAccounts
) { }
