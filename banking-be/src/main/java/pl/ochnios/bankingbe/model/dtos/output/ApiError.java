package pl.ochnios.bankingbe.model.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiError {

    private final String timestamp;
    private final String details;
    private final String errorId;
}
