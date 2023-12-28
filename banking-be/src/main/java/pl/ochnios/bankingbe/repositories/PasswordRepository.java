package pl.ochnios.bankingbe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ochnios.bankingbe.model.entities.Password;

import java.util.UUID;

public interface PasswordRepository extends JpaRepository<Password, UUID> {
}
