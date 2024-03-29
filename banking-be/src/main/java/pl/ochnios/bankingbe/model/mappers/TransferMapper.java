package pl.ochnios.bankingbe.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.ochnios.bankingbe.model.dtos.input.TransferOrderDto;
import pl.ochnios.bankingbe.model.dtos.output.TransferDto;
import pl.ochnios.bankingbe.model.entities.Transfer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    @Mapping(source = ".", target = ".")
    @Mapping(source = "time", target = "time", qualifiedByName = "mapToISO8601")
    TransferDto map(Transfer transfer);

    @Mapping(source = ".", target = ".")
    List<TransferDto> map(List<Transfer> transfers);

    @Mapping(source = ".", target = ".")
    Transfer map(TransferOrderDto transferOrderDto);

    @Named("mapToISO8601")
    default String mapToISO8601(Date date) {
        if (date == null) return null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return format.format(date);
    }
}
