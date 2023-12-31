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

    public Account getAccountByUserId(String userId) {
        return accountRepository.findByOwner_Id(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Account for userId=%s not found", userId)));
    }
}
