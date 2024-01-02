package pl.ochnios.bankingbe.model.dtos.input;

import jakarta.validation.constraints.*;
import lombok.Getter;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
public class PageCriteria {

    @NotNull
    @Positive
    private final Integer pageNumber;

    @NotNull
    @Min(5)
    @Max(100)
    private final Integer pageSize;

    @NotNull
    @Pattern(regexp = "^[A-Za-z]*$")
    private final String sortField;

    @NotNull
    @Pattern(regexp = "^asc|ASC|desc|DESC$")
    private final String sortDirection;

    public PageCriteria(String pageNumber, String pageSize, String sortField, String sortDirection) {
        this.pageNumber = sanitizedOrDefault(pageNumber, 1);
        this.pageSize = sanitizedOrDefault(pageSize, 5);
        this.sortField = StringEscapeUtils.escapeJava(sortField);
        this.sortDirection = StringEscapeUtils.escapeJava(sortDirection);
    }

    public Pageable toPageable() {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        return PageRequest.of(pageNumber - 1, pageSize, sort);
    }

    private Integer sanitizedOrDefault(String str, Integer defaultInt) {
        try {
            return Integer.parseInt(StringEscapeUtils.escapeJava(str));
        } catch (NumberFormatException e) {
            return defaultInt;
        }
    }
}
