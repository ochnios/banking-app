package pl.ochnios.bankingbe.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ochnios.bankingbe.model.dtos.input.PageCriteria;
import pl.ochnios.bankingbe.model.dtos.output.PageDto;
import pl.ochnios.bankingbe.model.dtos.output.TransferDto;
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
                                                                PageCriteria pageCriteria) {

        String userId = securityService.getAuthenticatedUserId();

        String sanitizedType = StringEscapeUtils.escapeJson(type);
        PageCriteria validatedCriteria = validOrDefaultPageCriteria(pageCriteria);

        PageDto<TransferDto> transfers = transferService.getTransfersForUser(userId, sanitizedType, validatedCriteria);
        return ResponseEntity.ok(transfers);
    }

    private PageCriteria validOrDefaultPageCriteria(PageCriteria pageCriteria) {
        if (pageCriteria == null) {
            return new PageCriteria("1", "1", "time", "desc");
        }

        Set<ConstraintViolation<PageCriteria>> violations = validator.validate(pageCriteria);
        if (!violations.isEmpty()) {
            return new PageCriteria("1", "5", "time", "desc");
        }

        return pageCriteria;
    }
}
