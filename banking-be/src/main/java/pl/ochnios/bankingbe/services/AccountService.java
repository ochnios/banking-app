package pl.ochnios.bankingbe.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ochnios.bankingbe.model.entities.Account;
import pl.ochnios.bankingbe.repositories.AccountRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account getAccountById(String id) {
        return accountRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Account with id=%s not found", id)));
    }
}
