package pl.ochnios.bankingbe.utils;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

@ControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ControllerAdvice.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<ApiError> handleConstraintViolation(Exception e) {
        return buildResponse(e, "Constraint violation exception", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiError> handleException(Exception e) {
        return buildResponse(e, "Unknown exception", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiError> buildResponse(Exception e, String message, HttpStatus status) {
        String traceId = Tracer.simpleTraceId();

        logger.error(String.format("traceId=%s; %s", traceId, e.getMessage()), e);

        ApiError apiError = new ApiError(
                dateFormat.format(new Date()),
                status.value(),
                message,
                traceId
        );

        return new ResponseEntity<>(apiError, status);
    }
}