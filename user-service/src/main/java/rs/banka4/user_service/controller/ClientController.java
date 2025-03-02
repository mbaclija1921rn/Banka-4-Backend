package rs.banka4.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.banka4.user_service.dto.ClientDto;
import rs.banka4.user_service.service.abstraction.ClientService;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
@Tag(name = "ClientController", description = "Endpoints for clients")
public class ClientController {
    private final ClientService clientService;
    @Operation(
        summary = "Search Clients",
        description = "Searches for clients based on the provided filters. Admin access required.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved clients list",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ClientDto.class))),
                @ApiResponse(responseCode = "400", description = "Invalid data for search filters"),
                @ApiResponse(responseCode = "403", description = "Forbidden - Admin privileges required")
        }
    )
    @GetMapping("/search")
    public ResponseEntity<Page<ClientDto>> getClients(
            @RequestParam(required = false) @Parameter(description = "First name of the client") String firstName,
            @RequestParam(required = false) @Parameter(description = "Last name of the client") String lastName,
            @RequestParam(required = false) @Parameter(description = "Email address of the client") String email,
            @RequestParam(required = false) @Parameter(description = "Field to sort by") String sortBy,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Number of clients per page") int size) {
        return clientService.getAll(firstName, lastName, email, sortBy, PageRequest.of(page, size));
    }
}
