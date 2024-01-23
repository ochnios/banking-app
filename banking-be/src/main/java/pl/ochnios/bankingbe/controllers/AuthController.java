package pl.ochnios.bankingbe.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
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
import pl.ochnios.bankingbe.model.dtos.input.LoginDto;
import pl.ochnios.bankingbe.model.dtos.output.ApiResponse;
import pl.ochnios.bankingbe.model.dtos.output.PositionsDto;
import pl.ochnios.bankingbe.model.dtos.output.UserDto;
import pl.ochnios.bankingbe.model.entities.User;
import pl.ochnios.bankingbe.services.SecurityService;

import java.util.Set;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final SecurityService securityService;
    private final Validator validator;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@RequestBody LoginDto loginDto,
                                                      HttpServletRequest request, HttpServletResponse response) {
        securityService.delayOperation();
        ApiResponse<UserDto> responseBody;

        Set<ConstraintViolation<LoginDto>> violations = validator.validate(loginDto);
        if (!violations.isEmpty()) {
            responseBody = ApiResponse.error("Bad credentials", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }

        if (securityService.findAccessToken(request).isPresent()) {
            responseBody = ApiResponse.error("Already logged in", null);
            return ResponseEntity.badRequest().body(responseBody);
        } else {
            try {
                String accessToken = securityService.authenticateWithPartialPassword(loginDto);
                securityService.setAccessToken(response, accessToken);
            } catch (BadCredentialsException | EntityNotFoundException e) {
                responseBody = ApiResponse.error("Bad credentials", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
            } catch (BlockedAccountException e) {
                responseBody = ApiResponse.error(
                        "Your account has been blocked due to security reasons. Please contact the bank.", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
            }
        }

        responseBody = ApiResponse.success(securityService.getAuthenticatedUser());
        return ResponseEntity.ok().body(responseBody);
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        if (securityService.findAccessToken(request).isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Already logged out"));
        }
        securityService.removeAccessToken(response);
        return ResponseEntity.ok().body(ApiResponse.success());
    }

    @GetMapping("/current-positions")
    public ResponseEntity<ApiResponse<PositionsDto>> getCurrentPositions(
            @RequestParam(required = false, name = "u") String usernameInput) {

        securityService.delayOperation();
        String username = StringEscapeUtils.escapeJava(usernameInput);
        if (!User.isUsernameCorrect(username)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Incorrect username", null));
        }

        int[] positions;
        try {
            positions = securityService.getPartialPasswordPositions(username);
        } catch (EntityNotFoundException e) {
            positions = securityService.fakePartialPasswordPositions(username);
        } catch (BlockedAccountException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Your account is blocked. Please contact the bank."));
        }

        PositionsDto positionsDto = new PositionsDto(positions);
        return ResponseEntity.ok().body(ApiResponse.success(positionsDto));
    }
}
