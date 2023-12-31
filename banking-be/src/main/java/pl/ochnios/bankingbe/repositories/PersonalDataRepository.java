package pl.ochnios.bankingbe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ochnios.bankingbe.model.entities.PersonalData;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonalDataRepository extends JpaRepository<PersonalData, UUID> {

    Optional<PersonalData> findByUser_Id(UUID userId);
}
