package pl.ochnios.bankingbe.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ochnios.bankingbe.model.dtos.output.ApiResponse;
import pl.ochnios.bankingbe.model.dtos.output.UserDto;
import pl.ochnios.bankingbe.security.SecurityService;
import pl.ochnios.bankingbe.services.PasswordService;

@RequestMapping("/api/user")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final SecurityService securityService;
    private final PasswordService passwordService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserDto>> getById() {
        UserDto userDto = securityService.getAuthenticatedUser();
        return ResponseEntity.ok().body(ApiResponse.success(userDto));
    }

    @GetMapping("/password-entropy-test")
    public ResponseEntity<ApiResponse<String>> pwd(@RequestParam String pwd) {
        return ResponseEntity.ok(ApiResponse.success(String.valueOf(passwordService.calculateEntropy(pwd))));
    }
}
