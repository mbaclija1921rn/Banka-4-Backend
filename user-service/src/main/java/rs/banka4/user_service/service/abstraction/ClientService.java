package rs.banka4.user_service.service.abstraction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import rs.banka4.user_service.dto.ClientDto;
import rs.banka4.user_service.dto.EmployeeDto;

public interface ClientService {
    ResponseEntity<Page<ClientDto>> getAll(String firstName, String lastName, String email, String sortBy, PageRequest pageRequest);
}
