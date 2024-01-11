package pl.ochnios.bankingbe.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.ochnios.bankingbe.model.dtos.output.ApiError;
import pl.ochnios.bankingbe.model.dtos.output.ApiResponse;

import java.text.SimpleDateFormat;
import java.util.Date;

@ControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiResponse<ApiError>> handleException(Exception exception) {
        String traceId = Tracer.simpleTraceId();
        ApiError apiError = new ApiError(currentTimestamp(), null, traceId);
        reportError(traceId, exception);
        return buildResponse("Unknown exception", apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse<ApiError>> buildResponse(String message, ApiError apiError, HttpStatus status) {
        return ResponseEntity.status(status).body(ApiResponse.error(message, apiError));
    }

    private void reportError(String traceId, Exception exception) {
        LOG.error(String.format("traceId=%s; %s", traceId, exception.getMessage()), exception);
    }

    private String currentTimestamp() {
        return DATE_FORMAT.format(new Date());
    }
}