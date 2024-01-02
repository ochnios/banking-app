package pl.ochnios.bankingbe.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ochnios.bankingbe.model.dtos.output.PersonalDataDto;
import pl.ochnios.bankingbe.model.entities.PersonalData;

@Mapper(componentModel = "spring")
public interface PersonalDataMapper {

    @Mapping(source = ".", target = ".")
    PersonalDataDto map(PersonalData personalData);
}
