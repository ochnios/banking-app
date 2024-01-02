package pl.ochnios.bankingbe.model.dtos.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GenericResponse<T> {

    private boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> GenericResponse<T> success(T data) {
        return new GenericResponse<T>(true, null, data);
    }

    public static <T> GenericResponse<T> error(String message) {
        return error(message, null);
    }

    public static <T> GenericResponse<T> error(String message, T data) {
        return new GenericResponse<T>(false, message, data);
    }
}
