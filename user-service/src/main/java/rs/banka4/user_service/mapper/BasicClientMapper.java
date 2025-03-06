package rs.banka4.user_service.mapper;

import rs.banka4.user_service.dto.AccountDto;
import rs.banka4.user_service.dto.ClientDto;
import rs.banka4.user_service.dto.requests.CreateClientDto;
import rs.banka4.user_service.models.Client;
import rs.banka4.user_service.models.Privilege;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class BasicClientMapper implements ClientMapper {

    private final AccountMapper accountMapper;

    public BasicClientMapper() {
        this.accountMapper = new AccountMapperImpl();
        // TODO ovo je stavljeno samo da bi mogao da merge
    }


    public BasicClientMapper(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }


    @Override
    public Client toEntity(CreateClientDto dto) {
        return null;
    }

    @Override
    public CreateClientDto toCreateDto(ClientDto dto) {
        return null;
    }

    @Override
    public ClientDto toDto(Client client) {
        if (client == null) {
            return null;
        }
        BasicAccountMapper basicAccountMapper = new BasicAccountMapper();
        List<AccountDto> accountDtos = client.getAccounts()
                .stream()
                .map(basicAccountMapper::toDto)
                .toList();

        return new ClientDto(
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                client.getDateOfBirth(),
                client.getGender(),
                client.getEmail(),
                client.getPhone(),
                client.getAddress(),
                client.getPrivileges(),
                accountDtos);
    }

    public ClientDto entityToDto(Client client) {
        if (client == null) return null;
        //empty for now as decided
        EnumSet<Privilege> set = EnumSet.noneOf(Privilege.class);
        List<AccountDto> linkedAccounts = List.of(); // client.getLinkedAccounts();
        ClientDto dto = new ClientDto(
                client.id,
                client.firstName,
                client.lastName,
                client.dateOfBirth,
                client.gender,
                client.email,
                client.phone,
                client.address,
                set,
                client.getAccounts().stream().map(accountMapper::toDto).collect(Collectors.toSet()).stream().toList()
        );
        return dto;
    }
}
