package pl.ochnios.bankingbe.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ochnios.bankingbe.model.dtos.output.AccountDto;
import pl.ochnios.bankingbe.model.entities.Account;
import pl.ochnios.bankingbe.model.mappers.AccountMapper;
import pl.ochnios.bankingbe.repositories.AccountRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountDto getAccountByUserId(String userId) {
        Account account = accountRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Account for userId=%s not found", userId)));
        return accountMapper.map(account);
    }
}
