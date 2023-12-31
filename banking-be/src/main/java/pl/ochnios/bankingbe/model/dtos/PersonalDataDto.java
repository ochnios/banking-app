package pl.ochnios.bankingbe.model.dtos;

import lombok.Data;

@Data
public class PersonalDataDto {

    private String address;
    private String cardNumber;
    private String identification;
}
