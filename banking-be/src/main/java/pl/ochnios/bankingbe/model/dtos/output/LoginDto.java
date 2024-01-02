package pl.ochnios.bankingbe.model.dtos.output;

import lombok.Data;

@Data
public class LoginDto {

    private String username;
    private String password;
}
