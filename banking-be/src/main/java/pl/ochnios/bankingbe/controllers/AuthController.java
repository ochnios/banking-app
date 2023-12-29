package pl.ochnios.bankingbe.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.ochnios.bankingbe.model.dtos.AuthDto;
import pl.ochnios.bankingbe.model.dtos.LoginDto;
import pl.ochnios.bankingbe.model.entities.User;
import pl.ochnios.bankingbe.security.JwtAuthFilter;
import pl.ochnios.bankingbe.security.JwtProvider;
import pl.ochnios.bankingbe.services.UserService;

@RequestMapping("/api/auth")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthDto> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()
        );

        Authentication auth = authManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        User authUser = userService.getUserByUsername(auth.getName());
        String token = jwtProvider.generateJwtForUser(authUser);
        response.addCookie(JwtAuthFilter.generateAuthCookie(token));

        AuthDto authDto = new AuthDto("success");
        return ResponseEntity.ok(authDto);
    }
}
