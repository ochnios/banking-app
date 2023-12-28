package pl.ochnios.bankingbe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ochnios.bankingbe.model.entities.Account;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}
