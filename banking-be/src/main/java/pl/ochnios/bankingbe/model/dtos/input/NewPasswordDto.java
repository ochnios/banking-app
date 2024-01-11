package pl.ochnios.bankingbe.model.dtos.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.apache.commons.text.StringEscapeUtils;

@Getter
public class NewPasswordDto {

    @NotNull
    @Pattern(regexp = "[!-~]{12,24}")
    private final String password;

    @NotNull
    @Pattern(regexp = "[!-~]{12,24}")
    private final String passwordRetyped;

    public NewPasswordDto(String password, String passwordRetyped) {
        this.password = StringEscapeUtils.escapeJava(password);
        this.passwordRetyped = StringEscapeUtils.escapeJava(passwordRetyped);
    }
}
