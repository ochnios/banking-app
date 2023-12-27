package pl.ochnios.bankingbe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ochnios.bankingbe.models.Transfer;

import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {
}
