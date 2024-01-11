package pl.ochnios.bankingbe.model.dtos.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.apache.commons.text.StringEscapeUtils;

@Getter
public class LoginDto {

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_.-]{3,20}$")
    private final String username;

    @NotNull
    @Pattern(regexp = "[!-~]{12,24}")
    private final String password;

    public LoginDto(String username, String password) {
        this.username = StringEscapeUtils.escapeJava(username);
        this.password = StringEscapeUtils.escapeJava(password);
    }
}
