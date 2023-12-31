package pl.ochnios.bankingbe.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ochnios.bankingbe.model.dtos.PersonalDataDto;
import pl.ochnios.bankingbe.model.mappers.PersonalDataMapper;
import pl.ochnios.bankingbe.security.SecurityService;
import pl.ochnios.bankingbe.services.PersonalDataService;

@RequestMapping("/api/user/personal")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class PersonalDataController {

    private final SecurityService securityService;
    private final PersonalDataService personalDataService;
    private final PersonalDataMapper personalDataMapper;

    @GetMapping
    public ResponseEntity<PersonalDataDto> getAccount() {
        String userId = securityService.getCurrentUserId();
        return ResponseEntity.ok(personalDataMapper.map(personalDataService.getPersonalDataByUserId(userId)));
    }
}
