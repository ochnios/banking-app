package pl.ochnios.bankingbe.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ochnios.bankingbe.model.dtos.output.ApiResponse;
import pl.ochnios.bankingbe.model.dtos.output.UserDto;
import pl.ochnios.bankingbe.services.SecurityService;

@RequestMapping("/api/user")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final SecurityService securityService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserDto>> getById() {
        UserDto userDto = securityService.getAuthenticatedUser();
        return ResponseEntity.ok().body(ApiResponse.success(userDto));
    }
}
