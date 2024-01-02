package pl.ochnios.bankingbe.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import pl.ochnios.bankingbe.model.dtos.PageCriteria;
import pl.ochnios.bankingbe.model.dtos.PageDto;
import pl.ochnios.bankingbe.model.dtos.TransferDto;
import pl.ochnios.bankingbe.model.entities.Transfer;
import pl.ochnios.bankingbe.model.mappers.PageMapper;
import pl.ochnios.bankingbe.model.mappers.TransferMapper;
import pl.ochnios.bankingbe.repositories.TransferRepository;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;
    private final PageMapper pageMapper;
    private final Validator validator;

    public TransferDto getTransferById(String transferId) {
        Transfer transfer = transferRepository.findById(UUID.fromString(transferId))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Transfer with id=%s not found", transferId)));
        return transferMapper.map(transfer);
    }

    public PageDto<TransferDto> getTransfersForUser(String userId, String type, PageCriteria pageCriteria) {
        Set<ConstraintViolation<PageCriteria>> violations = validator.validate(pageCriteria);
        if (!violations.isEmpty()) {
            pageCriteria = new PageCriteria(1, 5, "time", "desc");
        }

        UUID userUUID = UUID.fromString(userId);
        Page<Transfer> transfersPage;
        if ("in".equals(type)) {
            transfersPage = transferRepository.findByRecipient_Id(userUUID, pageCriteria.toPageable());
        } else if ("out".equals(type)) {
            transfersPage = transferRepository.findBySender_Id(userUUID, pageCriteria.toPageable());
        } else {
            transfersPage = transferRepository.findBySender_IdOrRecipient_Id(userUUID, userUUID, pageCriteria.toPageable());
        }

        return pageMapper.mapTransferPage(transfersPage);
    }
}
