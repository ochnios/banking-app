package pl.ochnios.bankingbe.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ochnios.bankingbe.model.dtos.PageCriteria;
import pl.ochnios.bankingbe.model.dtos.PageDto;
import pl.ochnios.bankingbe.model.dtos.TransferDto;
import pl.ochnios.bankingbe.security.SecurityService;
import pl.ochnios.bankingbe.services.TransferService;

import java.util.Set;

@RequestMapping("/api/user/transfer")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class TransferController {

    private final SecurityService securityService;
    private final TransferService transferService;
    private final Validator validator;

    @GetMapping("/{id}")
    public ResponseEntity<TransferDto> getTransfer(@PathVariable String id) {
        return ResponseEntity.ok(transferService.getTransferById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<PageDto<TransferDto>> searchTransfers(@RequestParam(required = false) String type,
                                                                @RequestParam(required = false) Integer pageNumber,
                                                                @RequestParam(required = false) Integer pageSize,
                                                                @RequestParam(required = false) String sortField,
                                                                @RequestParam(required = false) String sortDirection) {

        String userId = securityService.getAuthenticatedUserId();

        PageCriteria pageCriteria = new PageCriteria(pageNumber, pageSize, sortField, sortDirection);
        Set<ConstraintViolation<PageCriteria>> violations = validator.validate(pageCriteria);
        if (!violations.isEmpty()) {
            pageCriteria = new PageCriteria(1, 5, "time", "desc");
        }

        PageDto<TransferDto> transfers = transferService.getTransfersForUser(userId, type, pageCriteria);
        return ResponseEntity.ok(transfers);
    }
}
