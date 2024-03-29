package pl.ochnios.bankingbe.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ochnios.bankingbe.model.dtos.output.AccountDto;
import pl.ochnios.bankingbe.model.dtos.output.ApiResponse;
import pl.ochnios.bankingbe.services.AccountService;
import pl.ochnios.bankingbe.services.SecurityService;

@RequestMapping("/api/user/account")
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final SecurityService securityService;
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<ApiResponse<AccountDto>> getAccount() {
        String userId = securityService.getAuthenticatedUserId();
        AccountDto accountDto = accountService.getAccountByUserId(userId);
        return ResponseEntity.ok().body(ApiResponse.success(accountDto));
    }
}
