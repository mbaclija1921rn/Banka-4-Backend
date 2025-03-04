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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import rs.banka4.user_service.dto.*;
import rs.banka4.user_service.dto.requests.CreateClientDto;
import rs.banka4.user_service.dto.requests.UpdateClientDto;
import rs.banka4.user_service.exceptions.IncorrectCredentials;
import rs.banka4.user_service.exceptions.NotActivated;
import rs.banka4.user_service.exceptions.NotAuthenticated;
import rs.banka4.user_service.exceptions.NotFound;
import rs.banka4.user_service.models.Privilege;
import rs.banka4.user_service.utils.JwtUtil;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper = new BasicClientMapper();
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

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

    @Override
    public ResponseEntity<LoginResponseDto> login(LoginDto loginDto) {
        CustomUserDetailsService.role = "client"; // Consider refactoring this into a more robust role management system

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password())
            );
        } catch (BadCredentialsException e) {
            throw new IncorrectCredentials();
        }

        Client client = clientRepository.findByEmail(loginDto.email())
                .orElseThrow(() -> new UsernameNotFoundException(loginDto.email()));

        if (client.getPassword() == null) {
            throw new NotActivated();
        }

        String accessToken = jwtUtil.generateToken(client);
        String refreshToken = jwtUtil.generateRefreshToken(userDetailsService.loadUserByUsername(loginDto.email()), "client");

        return ResponseEntity.ok(new LoginResponseDto(accessToken, refreshToken));
    }

    @Override
    public ResponseEntity<PrivilegesDto> getPrivileges(String token) {
        return null;
    }

    @Override
    public ResponseEntity<ClientDto> getMe(String authorization) {
        String token = authorization.replace("Bearer ", "");
        String clientUsername = jwtUtil.extractUsername(token);

        if(jwtUtil.isTokenExpired(token)) throw new NotAuthenticated();
        if(jwtUtil.isTokenInvalidated(token)) throw new NotAuthenticated();

        Client client = clientRepository.findByEmail(clientUsername).orElseThrow(NotFound::new);

        ClientDto response = clientMapper.toDto(client);
        return ResponseEntity.ok(response);

    }

    @Override
    public ResponseEntity<ClientDto> getClient(String id) {
        ClientDto clientDto = new ClientDto(
                id,
                "MockFirstName",
                "MockLastName",
                LocalDate.of(1985, 5, 20),
                "Male",
                "mock.email@example.com",
                "987-654-3210",
                "123 Mockingbird Lane",
                EnumSet.noneOf(Privilege.class),
                List.of()
        );
        return ResponseEntity.ok(clientDto);
    }

    @Override
    public ResponseEntity<Void> createClient(CreateClientDto createClientDto) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> updateClient(String id, UpdateClientDto updateClientDto) {
        return null;
    }
}
