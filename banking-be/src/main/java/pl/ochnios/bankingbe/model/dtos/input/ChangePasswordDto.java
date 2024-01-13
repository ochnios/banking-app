package pl.ochnios.bankingbe.model.dtos.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.text.StringEscapeUtils;

@Getter
public class ChangePasswordDto {

    @NotNull
    private final String oldPassword;

    @NotNull
    @Valid
    private final NewPasswordDto newPassword;

    public ChangePasswordDto(String oldPassword, NewPasswordDto newPassword) {
        this.oldPassword = StringEscapeUtils.escapeJava(oldPassword);
        this.newPassword = newPassword;
    }
}
