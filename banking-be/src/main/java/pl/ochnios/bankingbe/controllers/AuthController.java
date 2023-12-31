package pl.ochnios.bankingbe.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ochnios.bankingbe.model.dtos.AuthDto;
import pl.ochnios.bankingbe.model.dtos.LoginDto;
import pl.ochnios.bankingbe.model.entities.User;
import pl.ochnios.bankingbe.security.JwtProvider;
import pl.ochnios.bankingbe.security.SecurityService;
import pl.ochnios.bankingbe.services.UserService;

@RequestMapping("/api/auth")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {

    private final SecurityService securityService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthDto> login(@RequestBody LoginDto loginDto,
                                         HttpServletRequest request, HttpServletResponse response) {

        if (securityService.getAccessToken(request).isPresent()) {
            return ResponseEntity.badRequest().body(new AuthDto("Already logged in"));
        }

        securityService.authenticateUser(loginDto);
        User authUser = userService.getUserByUsername(loginDto.getUsername());
        String jwt = jwtProvider.generateJwtForUser(authUser);
        securityService.setAccessToken(response, jwt);

        return ResponseEntity.ok(new AuthDto("Successfully logged in"));
    }

    @GetMapping("/logout")
    public ResponseEntity<AuthDto> logout(HttpServletRequest request, HttpServletResponse response) {
        if (securityService.getAccessToken(request).isEmpty()) {
            return ResponseEntity.badRequest().body(new AuthDto("Already logged out"));
        }
        securityService.removeAccessToken(response);
        return ResponseEntity.ok(new AuthDto("Successfully logged out"));
    }
}
