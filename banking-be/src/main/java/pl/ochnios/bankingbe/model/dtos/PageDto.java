package pl.ochnios.bankingbe.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageDto<T> {

    private List<T> content;
    private int number;
    private int totalPages;
    private long totalElements;
}
