package pl.ochnios.bankingbe.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ochnios.bankingbe.model.dtos.output.PersonalDataDto;
import pl.ochnios.bankingbe.model.entities.PersonalData;
import pl.ochnios.bankingbe.model.mappers.PersonalDataMapper;
import pl.ochnios.bankingbe.repositories.PersonalDataRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonalDataService {

    private final PersonalDataRepository personalDataRepository;
    private final PersonalDataMapper personalDataMapper;

    public PersonalDataDto getPersonalDataById(String userId) {
        PersonalData personalData = personalDataRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Personal data for userId=%s not found", userId)));
        return personalDataMapper.map(personalData);
    }
}
