package rs.banka4.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rs.banka4.user_service.dto.ClientDto;
import rs.banka4.user_service.exceptions.NonexistantSortByField;
import rs.banka4.user_service.exceptions.NullPageRequest;
import rs.banka4.user_service.mapper.BasicClientMapper;
import rs.banka4.user_service.mapper.ClientMapper;
import rs.banka4.user_service.models.Client;
import rs.banka4.user_service.repositories.ClientRepository;
import rs.banka4.user_service.service.abstraction.ClientService;
import rs.banka4.user_service.utils.specification.ClientSpecification;
import rs.banka4.user_service.utils.specification.SpecificationCombinator;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper = new BasicClientMapper();

    @Override
    public ResponseEntity<Page<ClientDto>> getAll(String firstName, String lastName, String email,
                                                  String sortBy, PageRequest pageRequest) {
        if (pageRequest == null) {
            throw new NullPageRequest();
        }

        SpecificationCombinator<Client> combinator = new SpecificationCombinator<>();

        if (firstName != null && !firstName.isEmpty()) {
            combinator.and(ClientSpecification.hasFirstName(firstName));
        }
        if (lastName != null && !lastName.isEmpty()) {
            combinator.and(ClientSpecification.hasLastName(lastName));
        }
        if (email != null && !email.isEmpty()) {
            combinator.and(ClientSpecification.hasEmail(email));
        }

        Sort sort;
        if (sortBy == null || sortBy.isEmpty() || "default".equalsIgnoreCase(sortBy)
                || "firstName".equalsIgnoreCase(sortBy)) {
            sort = Sort.by("firstName");
        } else if ("lastName".equalsIgnoreCase(sortBy)) {
            sort = Sort.by("lastName");
        } else if ("email".equalsIgnoreCase(sortBy)) {
            sort = Sort.by("email");
        } else {
            throw new NonexistantSortByField(sortBy);
        }

        PageRequest pageRequestWithSort = PageRequest.of(pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                sort);

        Page<Client> clients = clientRepository.findAll(combinator.build(), pageRequestWithSort);
        Page<ClientDto> dtos = clients.map(clientMapper::toDto);

        return ResponseEntity.ok(dtos);
    }

}

