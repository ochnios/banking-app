package pl.ochnios.bankingbe.model.dtos.input;

import lombok.Getter;
import org.apache.commons.text.StringEscapeUtils;

@Getter
public class LoginDto {

    private final String username;
    private final String password;

    public LoginDto(String username, String password) {
        this.username = StringEscapeUtils.escapeJava(username);
        this.password = StringEscapeUtils.escapeJava(password);
    }
}
