package pl.ochnios.bankingbe.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import pl.ochnios.bankingbe.model.dtos.PageDto;
import pl.ochnios.bankingbe.model.dtos.TransferDto;
import pl.ochnios.bankingbe.model.entities.Transfer;

@Mapper(componentModel = "spring", uses = TransferMapper.class)
public interface PageMapper {

    @Mapping(source = "number", target = "number", qualifiedByName = "mapPageNumber")
    PageDto<TransferDto> mapTransferPage(Page<Transfer> page);

    @Named("mapPageNumber")
    default int mapPageNumber(int pageNumber) {
        return pageNumber + 1;
    }
}
