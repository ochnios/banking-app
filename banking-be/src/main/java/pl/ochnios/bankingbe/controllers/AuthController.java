package pl.ochnios.bankingbe.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.Collectors;

@RequestMapping("/api/auth")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {

    private final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private final SecurityService securityService;
    private final Validator validator;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@RequestBody LoginDto loginDto,
                                                      HttpServletRequest request, HttpServletResponse response) {
        securityService.delayOperation();
        ApiResponse<UserDto> responseBody;

        Set<ConstraintViolation<LoginDto>> violations = validator.validate(loginDto);
        if (!violations.isEmpty()) {
            LOG.warn(String.format("Failed login attempt for %s: %s", loginDto.getUsername(),
                    violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("; "))));
            responseBody = ApiResponse.error("Bad credentials", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }

        if (securityService.findAccessToken(request).isPresent()) {
            LOG.warn(String.format("Failed login attempt, user %s is already logged in", loginDto.getUsername()));
            responseBody = ApiResponse.error("Already logged in", null);
            return ResponseEntity.badRequest().body(responseBody);
        } else {
            try {
                String accessToken = securityService.authenticateWithPartialPassword(loginDto);
                securityService.setAccessToken(response, accessToken);
            } catch (BadCredentialsException | EntityNotFoundException e) {
                LOG.warn(String.format("Failed login attempt for %s: %s", loginDto.getUsername(), e.getMessage()));
                responseBody = ApiResponse.error("Bad credentials", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
            } catch (BlockedAccountException e) {
                LOG.warn(String.format("Failed login attempt for %s: %s", loginDto.getUsername(), e.getMessage()));
                responseBody = ApiResponse.error(
                        "Your account has been blocked due to security reasons. Please contact the bank.", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
            }
        }

        LOG.info(String.format("Successful login attempt for %s", loginDto.getUsername()));
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
            LOG.warn(String.format("Failed get positions attempt, incorrect username: %s", username));
            return ResponseEntity.badRequest().body(ApiResponse.error("Incorrect username", null));
        }

        int[] positions;
        try {
            positions = securityService.getPartialPasswordPositions(username);
            LOG.info(String.format("Successful get positions attempt for %s", username));
        } catch (EntityNotFoundException e) {
            LOG.warn(String.format("Failed get positions attempt for %s: %s", username, e.getMessage()));
            positions = securityService.fakePartialPasswordPositions(username);
        } catch (BlockedAccountException e) {
            LOG.warn(String.format("Get positions attempt for blocked account %s: %s", username, e.getMessage()));
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Your account is blocked. Please contact the bank."));
        }

        PositionsDto positionsDto = new PositionsDto(positions);
        return ResponseEntity.ok().body(ApiResponse.success(positionsDto));
    }
}
