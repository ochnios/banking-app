package pl.ochnios.bankingbe.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.ochnios.bankingbe.exceptions.TransferFailureException;
import pl.ochnios.bankingbe.model.dtos.input.PageCriteria;
import pl.ochnios.bankingbe.model.dtos.input.TransferOrderDto;
import pl.ochnios.bankingbe.model.dtos.output.PageDto;
import pl.ochnios.bankingbe.model.dtos.output.TransferDto;
import pl.ochnios.bankingbe.model.entities.*;
import pl.ochnios.bankingbe.model.mappers.PageMapper;
import pl.ochnios.bankingbe.model.mappers.TransferMapper;
import pl.ochnios.bankingbe.repositories.AccountRepository;
import pl.ochnios.bankingbe.repositories.PersonalDataRepository;
import pl.ochnios.bankingbe.repositories.TransferRepository;
import pl.ochnios.bankingbe.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PersonalDataRepository personalDataRepository;
    private final TransferMapper transferMapper;
    private final PageMapper pageMapper;

    public TransferDto getTransferById(String transferId) {
        Transfer transfer = transferRepository.findById(UUID.fromString(transferId))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Transfer with id=%s not found", transferId)));
        return transferMapper.map(transfer);
    }

    public PageDto<TransferDto> getTransfersForUser(String userId, String type, PageCriteria pageCriteria) {
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

    @Transactional
    public TransferDto processTransferOrder(String senderId, TransferOrderDto transferOrderDto) {
        UUID senderUUID = UUID.fromString(senderId);
        Transfer transfer = transferMapper.map(transferOrderDto);

        Account senderAccount = accountRepository.findById(senderUUID).orElseThrow();
        if (senderAccount.getAccountNumber().equals(transfer.getRecipientAccountNumber())) {
            throw new TransferFailureException("Sender account can not be recipient account");
        }
        if (senderAccount.getBalance().compareTo(transfer.getAmount()) < 0) {
            throw new TransferFailureException("No enough funds");
        }

        User sender = userRepository.findById(senderUUID).orElseThrow();
        PersonalData senderData = personalDataRepository.findById(senderUUID).orElseThrow();
        transfer.setSender(sender);
        transfer.setSenderAccountNumber(senderAccount.getAccountNumber());
        transfer.setSenderName(sender.getName() + " " + sender.getSurname());
        transfer.setSenderAddress(senderData.getAddress());

        Account recipientAccount = accountRepository.findAccountByAccountNumber(transfer.getRecipientAccountNumber()).orElse(null);
        if (recipientAccount == null) {
            transfer.setType(TransferType.EXTERNAL);
            externalTransfer();
        } else {
            transfer.setType(TransferType.INTERNAL);
            updateRecipientAccountBalance(recipientAccount, transfer.getAmount());
        }

        updateSenderAccountBalance(senderAccount, transfer.getAmount());

        return transferMapper.map(transferRepository.saveAndFlush(transfer));
    }

    private void updateSenderAccountBalance(Account senderAccount, BigDecimal transferAmount) {
        BigDecimal senderAccountBalance = senderAccount.getBalance();
        senderAccount.setBalance(senderAccountBalance.subtract(transferAmount));
        accountRepository.save(senderAccount);
    }

    private void updateRecipientAccountBalance(Account recipientAccount, BigDecimal transferAmount) {
        BigDecimal recipientAccountBalance = recipientAccount.getBalance();
        recipientAccount.setBalance(recipientAccountBalance.add(transferAmount));
        accountRepository.save(recipientAccount);
    }

    private void externalTransfer() {
        throw new TransferFailureException("External transfers are not currently supported");
    }
}
