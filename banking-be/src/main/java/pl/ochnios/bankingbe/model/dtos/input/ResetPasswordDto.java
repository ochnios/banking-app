package pl.ochnios.bankingbe.model.dtos.input;

import lombok.Getter;
import org.apache.commons.text.StringEscapeUtils;

@Getter
public class ResetPasswordDto {

    private final String token;
    private final String password;
    private final String passwordRetyped;

    public ResetPasswordDto(String token, String password, String passwordRetyped) {
        this.token = StringEscapeUtils.escapeJava(token);
        this.password = StringEscapeUtils.escapeJava(password);
        this.passwordRetyped = StringEscapeUtils.escapeJava(passwordRetyped);
    }
}
