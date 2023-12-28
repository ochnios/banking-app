package pl.ochnios.bankingbe.utils;

import lombok.Data;

@Data
public class ApiError {

    private final String timestamp;
    private final int status;
    private final String message;
    private final String traceId;
}
