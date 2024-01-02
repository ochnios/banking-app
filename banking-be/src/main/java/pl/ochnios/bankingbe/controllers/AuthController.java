package pl.ochnios.bankingbe.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import pl.ochnios.bankingbe.model.dtos.input.LoginDto;
import pl.ochnios.bankingbe.model.dtos.output.AuthDto;
import pl.ochnios.bankingbe.security.SecurityService;

@RequestMapping("/api/auth")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {

    private final SecurityService securityService;

    @PostMapping("/login")
    public ResponseEntity<AuthDto> login(@RequestBody LoginDto loginDto,
                                         HttpServletRequest request, HttpServletResponse response) {
        delay(500);
        if (securityService.findAccessToken(request).isPresent()) {
            return ResponseEntity.badRequest().body(new AuthDto("Already logged in"));
        }

        try {
            String accessToken = securityService.authenticateWithCredentials(loginDto);
            securityService.setAccessToken(response, accessToken);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AuthDto("Bad credentials"), HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(new AuthDto("Successfully logged in"));
    }

    @GetMapping("/logout")
    public ResponseEntity<AuthDto> logout(HttpServletRequest request, HttpServletResponse response) {
        if (securityService.findAccessToken(request).isEmpty()) {
            return ResponseEntity.badRequest().body(new AuthDto("Already logged out"));
        }
        securityService.removeAccessToken(response);
        return ResponseEntity.ok(new AuthDto("Successfully logged out"));
    }

    private void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
