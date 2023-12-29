package pl.ochnios.bankingbe.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ochnios.bankingbe.model.dtos.UserDto;
import pl.ochnios.bankingbe.model.entities.User;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = ".", target = ".")
    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "personalData.id", target = "personalDataId")
    UserDto map(User user);

    default List<UserDto> map(List<User> user) {
        return user.stream().map(this::map).collect(Collectors.toList());
    }
}
