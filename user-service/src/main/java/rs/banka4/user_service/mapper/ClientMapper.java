package rs.banka4.user_service.mapper;

import org.mapstruct.*;
import rs.banka4.user_service.dto.ClientDto;
import rs.banka4.user_service.models.Client;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {
    ClientDto toDto(Client client);
}