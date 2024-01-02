package pl.ochnios.bankingbe.model.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
public class PageCriteria {

    @NotNull
    @Positive
    private Integer pageNumber;

    @NotNull
    @Min(5)
    @Max(100)
    private Integer pageSize;

    @NotNull
    @Pattern(regexp = "^[A-Za-z]*$")
    private String sortField;

    @NotNull
    @Pattern(regexp = "^asc|ASC|desc|DESC$")
    private String sortDirection;

    public Pageable toPageable() {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        return PageRequest.of(pageNumber - 1, pageSize, sort);
    }
}
