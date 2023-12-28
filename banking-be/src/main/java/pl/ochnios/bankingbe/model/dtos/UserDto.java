package pl.ochnios.bankingbe.model.entities.dtos;

import lombok.Data;

@Data
public class UserDto {

    private String id;
    private String name;
    private String surname;
    private String email;
    private String accountId;
    private String personalDataId;
}
