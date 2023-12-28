package pl.ochnios.bankingbe.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ochnios.bankingbe.model.dtos.TransferDto;
import pl.ochnios.bankingbe.model.entities.Transfer;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    @Mapping(source = ".", target = ".")
    TransferDto map(Transfer transfer);

    @Mapping(source = ".", target = ".")
    Transfer map(TransferDto transferDto);
}
