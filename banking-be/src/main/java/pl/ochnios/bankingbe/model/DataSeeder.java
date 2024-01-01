package pl.ochnios.bankingbe.model;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.ochnios.bankingbe.model.entities.*;
import pl.ochnios.bankingbe.model.mappers.AccountMapper;
import pl.ochnios.bankingbe.model.mappers.PersonalDataMapper;
import pl.ochnios.bankingbe.model.mappers.UserMapper;
import pl.ochnios.bankingbe.repositories.AccountRepository;
import pl.ochnios.bankingbe.repositories.PersonalDataRepository;
import pl.ochnios.bankingbe.repositories.UserRepository;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final static Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PersonalDataRepository personalDataRepository;
    private final UserMapper userMapper;
    private final AccountMapper accountMapper;
    private final PersonalDataMapper personalDataMapper;

    @Override
    @Transactional
    public void run(String... args) {
        createUser("John", "Doe", "john.doe@example.com", "doej3622", "HardPassword1",
                "XXAAAABBBBCCCCDDDDEEEE0001", "1000.00", "123 Main Street, Warsaw, Mazovia, 00-001, Poland",
                "XXXXYYYYZZZZ0001", "XYZ00001");

        createUser("Mark", "Smith", "mark.smith@example.com", "smithm1005", "HardPassword2",
                "XXAAAABBBBCCCCDDDDEEEE0002", "100.00", "321 Side Street, Katowice, Silesia, 00-002, Poland",
                "XXXXYYYYZZZZ0002", "XYZ00002");

    }

    private void createUser(String name, String surname, String email, String username, String passwordStr,
                            String accountNumber, String balance,
                            String address, String cardNumber, String identification) {

        Password password = new Password(null, passwordEncoder.encode(passwordStr));
        User user = new User(null, name, surname, email, username, password, null, UserStatus.ACTIVE, 0);
        user = userRepository.save(user);

        PersonalData personalData = new PersonalData(null, user, address, cardNumber, identification);
        Account account = new Account(null, accountNumber, user, new BigDecimal(balance));

        personalDataRepository.save(personalData);
        accountRepository.save(account);

        logger.info(userMapper.map(user).toString());
        logger.info(personalDataMapper.map(personalData).toString());
        logger.info(accountMapper.map(account).toString());
    }
}
