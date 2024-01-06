package pl.ochnios.bankingbe.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ochnios.bankingbe.exceptions.PasswordValidationException;
import pl.ochnios.bankingbe.model.dtos.input.LoginDto;
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

    @PostMapping("/set-partial-password")
    public ResponseEntity<ApiResponse<String>> setPartial(@RequestBody LoginDto loginDto) {
        String userId = securityService.getAuthenticatedUserId();

        try {
            passwordService.setUserPassword(userId, loginDto.getPassword());
        } catch (PasswordValidationException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }

        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/check-partial-password")
    public ResponseEntity<ApiResponse<String>> checkPartial(@RequestBody LoginDto loginDto) {
        String userId = securityService.getAuthenticatedUserId();

        if (passwordService.verifyUserPassword(userId, loginDto.getPassword()))
            return ResponseEntity.ok(ApiResponse.success());
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Wrong password"));
    }
}
