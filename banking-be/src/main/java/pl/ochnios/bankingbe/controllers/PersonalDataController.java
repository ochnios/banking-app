package pl.ochnios.bankingbe.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ochnios.bankingbe.model.dtos.output.ApiResponse;
import pl.ochnios.bankingbe.model.dtos.output.PersonalDataDto;
import pl.ochnios.bankingbe.services.PersonalDataService;
import pl.ochnios.bankingbe.services.SecurityService;

@RequestMapping("/api/user/personal")
@RestController
@RequiredArgsConstructor
public class PersonalDataController {

    private final SecurityService securityService;
    private final PersonalDataService personalDataService;

    @GetMapping
    public ResponseEntity<ApiResponse<PersonalDataDto>> getAccount() {
        String userId = securityService.getAuthenticatedUserId();
        PersonalDataDto personalDataDto = personalDataService.getPersonalDataById(userId);
        return ResponseEntity.ok().body(ApiResponse.success(personalDataDto));
    }
}
