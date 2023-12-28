package pl.ochnios.bankingbe.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ochnios.bankingbe.model.dtos.UserDto;
import pl.ochnios.bankingbe.model.mappers.UserMapper;
import pl.ochnios.bankingbe.services.UserService;

@RequestMapping("/api/users")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable String id) {
        return ResponseEntity.ok(userMapper.map(userService.getUserById(id)));
    }
}
