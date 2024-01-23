package pl.ochnios.bankingbe.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ochnios.bankingbe.exceptions.TransferFailureException;
import pl.ochnios.bankingbe.model.dtos.input.PageCriteria;
import pl.ochnios.bankingbe.model.dtos.input.TransferOrderDto;
import pl.ochnios.bankingbe.model.dtos.output.ApiResponse;
import pl.ochnios.bankingbe.model.dtos.output.PageDto;
import pl.ochnios.bankingbe.model.dtos.output.TransferDto;
import pl.ochnios.bankingbe.services.SecurityService;
import pl.ochnios.bankingbe.services.TransferService;

import java.util.Set;

@RequestMapping("/api/user/transfer")
@RestController
@RequiredArgsConstructor
public class TransferController {

    private final SecurityService securityService;
    private final TransferService transferService;
    private final Validator validator;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransferDto>> getTransfer(@PathVariable String id) {
        TransferDto transferDto = transferService.getTransferById(id);
        return ResponseEntity.ok().body(ApiResponse.success(transferDto));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageDto<TransferDto>>> searchTransfers(@RequestParam(required = false) String type,
                                                                             PageCriteria pageCriteria) {

        String userId = securityService.getAuthenticatedUserId();

        String sanitizedType = StringEscapeUtils.escapeJson(type);
        PageCriteria validatedCriteria = validOrDefaultPageCriteria(pageCriteria);

        PageDto<TransferDto> transfers = transferService.getTransfersForUser(userId, sanitizedType, validatedCriteria);
        return ResponseEntity.ok().body(ApiResponse.success(transfers));
    }

    @PostMapping("/new")
    public ResponseEntity<ApiResponse<TransferDto>> newTransfer(@RequestBody TransferOrderDto transferOrderDto) {
        Set<ConstraintViolation<TransferOrderDto>> violations = validator.validate(transferOrderDto);
        if (!violations.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Check entered transfer details and try again"));
        }

        String userId = securityService.getAuthenticatedUserId();
        try {
            TransferDto createdTransfer = transferService.processTransferOrder(userId, transferOrderDto);
            return ResponseEntity.accepted().body(ApiResponse.success(createdTransfer));
        } catch (TransferFailureException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
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
