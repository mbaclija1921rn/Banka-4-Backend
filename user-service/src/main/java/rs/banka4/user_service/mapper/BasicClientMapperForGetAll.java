package rs.banka4.user_service.mapper;

import org.springframework.stereotype.Component;
import rs.banka4.user_service.dto.AccountDto;
import rs.banka4.user_service.dto.ClientDto;
import rs.banka4.user_service.dto.requests.CreateClientDto;
import rs.banka4.user_service.models.Client;

import java.util.List;

@Component
public class BasicClientMapperForGetAll implements ClientMapper {
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

    @Override
    public CreateClientDto toCreateDto(ClientDto dto) {
        return null;
    }

    @Override
    public Client toEntity(CreateClientDto dto) {
        return null;
    }
}
