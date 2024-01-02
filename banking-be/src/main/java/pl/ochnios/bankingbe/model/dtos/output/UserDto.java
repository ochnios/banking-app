package pl.ochnios.bankingbe.model.dtos.output;

import lombok.Data;

@Data
public class UserDto {

    private String name;
    private String surname;
    private String email;
}
