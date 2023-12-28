package pl.ochnios.bankingbe.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ochnios.bankingbe.model.dtos.AccountDto;
import pl.ochnios.bankingbe.model.entities.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(source = ".", target = ".")
    AccountDto map(Account account);
}
