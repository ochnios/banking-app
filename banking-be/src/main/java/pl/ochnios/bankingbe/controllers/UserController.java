package pl.ochnios.bankingbe.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ochnios.bankingbe.model.dtos.UserDto;
import pl.ochnios.bankingbe.security.SecurityService;
import pl.ochnios.bankingbe.services.UserService;

@RequestMapping("/api/user")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final SecurityService securityService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDto> getById() {
        return ResponseEntity.ok(securityService.getAuthenticatedUser());
    }
}
