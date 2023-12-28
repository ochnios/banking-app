package pl.ochnios.bankingbe.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ochnios.bankingbe.model.dtos.UserDto;
import pl.ochnios.bankingbe.model.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = ".", target = ".")
    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "personalData.id", target = "personalDataId")
    UserDto map(User user);
}
