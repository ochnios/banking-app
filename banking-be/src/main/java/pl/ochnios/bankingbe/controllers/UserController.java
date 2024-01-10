package pl.ochnios.bankingbe.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ochnios.bankingbe.exceptions.BlockedAccountException;
import pl.ochnios.bankingbe.model.dtos.input.ResetPasswordDto;
import pl.ochnios.bankingbe.model.dtos.output.ApiResponse;
import pl.ochnios.bankingbe.model.dtos.output.UserDto;
import pl.ochnios.bankingbe.model.entities.User;
import pl.ochnios.bankingbe.services.EmailService;
import pl.ochnios.bankingbe.services.SecurityService;
import pl.ochnios.bankingbe.services.UserService;

@RequestMapping("/api/user")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final SecurityService securityService;
    private final UserService userService;
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserDto>> getById() {
        UserDto userDto = securityService.getAuthenticatedUser();
        return ResponseEntity.ok().body(ApiResponse.success(userDto));
    }

    @GetMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> getResetPasswordToken(
            @RequestParam(required = false, name = "u") String usernameInput) {

        securityService.delayOperation();
        String username = StringEscapeUtils.escapeJava(usernameInput);
        if (!User.isUsernameCorrect(username)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Incorrect username", null));
        }

        try {
            String token = userService.generateResetPasswordToken(username);
            emailService.sendEmail("Reset your bank password", "https://localhost/reset-password?t=" + token);
        } catch (EntityNotFoundException | IllegalStateException e) {
            // Just do nothing if requested user does not exist or active token already exists
        } catch (BlockedAccountException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Your account is blocked. Please contact the bank."));
        }

        return ResponseEntity.ok().body(ApiResponse.success());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        securityService.delayOperation();
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
