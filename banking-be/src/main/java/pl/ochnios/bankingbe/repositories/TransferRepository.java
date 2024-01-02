package pl.ochnios.bankingbe.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ochnios.bankingbe.model.entities.Transfer;

import java.util.UUID;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, UUID> {

    Page<Transfer> findByRecipient_Id(UUID recipientId, Pageable pageable);
    
    Page<Transfer> findBySender_Id(UUID senderId, Pageable pageable);

    Page<Transfer> findBySender_IdOrRecipient_Id(UUID senderId, UUID recipientId, Pageable pageable);
}
