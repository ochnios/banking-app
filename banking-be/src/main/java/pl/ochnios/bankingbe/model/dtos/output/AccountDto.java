package pl.ochnios.bankingbe.model.dtos.output;

import lombok.Data;

@Data
public class AccountDto {

    private String accountNumber;
    private String balance;
}
