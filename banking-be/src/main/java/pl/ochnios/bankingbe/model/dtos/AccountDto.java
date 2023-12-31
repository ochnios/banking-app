package pl.ochnios.bankingbe.model.dtos;

import lombok.Data;

@Data
public class AccountDto {

    private String accountNumber;
    private String balance;
}
