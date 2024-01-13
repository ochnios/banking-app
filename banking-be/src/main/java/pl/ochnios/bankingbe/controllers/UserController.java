package pl.ochnios.bankingbe.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import pl.ochnios.bankingbe.exceptions.BlockedAccountException;
import pl.ochnios.bankingbe.exceptions.PasswordValidationException;
import pl.ochnios.bankingbe.exceptions.ResetTokenValidationException;
import pl.ochnios.bankingbe.model.dtos.input.ChangePasswordDto;
import pl.ochnios.bankingbe.model.dtos.input.NewPasswordDto;
import pl.ochnios.bankingbe.model.dtos.output.ApiResponse;
import pl.ochnios.bankingbe.model.dtos.output.UserDto;
import pl.ochnios.bankingbe.model.entities.User;
import pl.ochnios.bankingbe.services.EmailService;
import pl.ochnios.bankingbe.services.SecurityService;
import pl.ochnios.bankingbe.services.UserService;

import java.util.Set;

@RequestMapping("/api/user")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final SecurityService securityService;
    private final UserService userService;
    private final EmailService emailService;
    private final Validator validator;

    @GetMapping
    public ResponseEntity<ApiResponse<UserDto>> getById() {
        UserDto userDto = securityService.getAuthenticatedUser();
        return ResponseEntity.ok().body(ApiResponse.success(userDto));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody ChangePasswordDto changePasswordDto,
                                                            HttpServletResponse response) {

        securityService.delayOperation();
        Set<ConstraintViolation<ChangePasswordDto>> violations = validator.validate(changePasswordDto);
        if (!violations.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Entered password does not meet the requirements"));
        }

        try {
            userService.changePassword(changePasswordDto);
            securityService.removeAccessToken(response);
        } catch (PasswordValidationException | BadCredentialsException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (BlockedAccountException e) {
            securityService.removeAccessToken(response);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(e.getMessage()));
        }

        return ResponseEntity.ok().body(ApiResponse.success());
    }

    @GetMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> getResetPasswordToken(@RequestParam(name = "u") String usernameInput) {

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
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestParam(name = "t") String tokenInput,
                                                           @RequestBody NewPasswordDto newPasswordDto,
                                                           HttpServletResponse response) {
        securityService.delayOperation();
        String token = StringEscapeUtils.escapeJava(tokenInput);
        Set<ConstraintViolation<NewPasswordDto>> violations = validator.validate(newPasswordDto);
        if (!violations.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Entered password does not meet the requirements"));
        }

        try {
            userService.resetPassword(token, newPasswordDto);
            securityService.removeAccessToken(response); // when user is logged in and resets password
        } catch (ResetTokenValidationException | PasswordValidationException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (BlockedAccountException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Your account is blocked. Please contact the bank."));
        }

        return ResponseEntity.ok().body(ApiResponse.success());
    }
}
