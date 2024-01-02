package pl.ochnios.bankingbe.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.ochnios.bankingbe.model.dtos.output.ApiError;
import pl.ochnios.bankingbe.model.dtos.output.GenericResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<GenericResponse<ApiError>> handleConstraintViolation(ConstraintViolationException exception) {
        String details = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        String traceId = Tracer.simpleTraceId();
        ApiError apiError = new ApiError(currentTimestamp(), details, traceId);
        return buildResponse("Constraint violation exception", apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<GenericResponse<ApiError>> handleException(Exception exception) {
        String traceId = Tracer.simpleTraceId();
        ApiError apiError = new ApiError(currentTimestamp(), null, traceId);
        reportError(traceId, exception);
        return buildResponse("Unknown exception", apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<GenericResponse<ApiError>> buildResponse(String message, ApiError apiError, HttpStatus status) {
        return ResponseEntity.status(status).body(GenericResponse.error(message, apiError));
    }

    private void reportError(String traceId, Exception exception) {
        LOG.error(String.format("traceId=%s; %s", traceId, exception.getMessage()), exception);
    }

    private String currentTimestamp() {
        return DATE_FORMAT.format(new Date());
    }
}