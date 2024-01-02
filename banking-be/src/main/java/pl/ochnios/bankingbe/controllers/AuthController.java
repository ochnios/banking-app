package pl.ochnios.bankingbe.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import pl.ochnios.bankingbe.model.dtos.input.LoginDto;
import pl.ochnios.bankingbe.model.dtos.output.ApiResponse;
import pl.ochnios.bankingbe.model.dtos.output.UserDto;
import pl.ochnios.bankingbe.security.SecurityService;

@RequestMapping("/api/auth")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {

    private final SecurityService securityService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@RequestBody LoginDto loginDto,
                                                      HttpServletRequest request, HttpServletResponse response) {
        delay(500);

        ApiResponse<UserDto> responseBody;
        if (securityService.findAccessToken(request).isPresent()) {
            responseBody = ApiResponse.error("Already logged in", null);
            return ResponseEntity.badRequest().body(responseBody);
        } else {
            try {
                String accessToken = securityService.authenticateWithCredentials(loginDto);
                securityService.setAccessToken(response, accessToken);
            } catch (BadCredentialsException e) {
                responseBody = ApiResponse.error("Bad credentials", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
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

    private void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
