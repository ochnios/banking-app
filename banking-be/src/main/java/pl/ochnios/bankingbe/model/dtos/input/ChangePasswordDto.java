package pl.ochnios.bankingbe.model.dtos.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.apache.commons.text.StringEscapeUtils;

@Getter
public class ChangePasswordDto {

    @NotNull
    @Pattern(regexp = "[!-~]{12,24}")
    private final String oldPassword;

    @NotNull
    @Valid
    private final NewPasswordDto newPassword;

    @JsonCreator
    public ChangePasswordDto(String oldPassword, NewPasswordDto newPassword) {
        this.oldPassword = StringEscapeUtils.escapeJava(oldPassword);
        this.newPassword = newPassword;
    }
}
