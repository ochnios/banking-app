package pl.ochnios.bankingbe.model;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.ochnios.bankingbe.model.entities.*;
import pl.ochnios.bankingbe.model.mappers.UserMapper;
import pl.ochnios.bankingbe.repositories.UserRepository;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final static Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void run(String... args) {
        User user1 = createUser("John", "Doe", "john.doe@example.com", "doej3622", "HardPassword1",
                "XXAAAABBBBCCCCDDDDEEEE0001", "1000.00", "123 Main Street, Warsaw, Mazovia, 00-001, Poland",
                "XXXXYYYYZZZZ0001", "XYZ00001");

        User user2 = createUser("Mark", "Smith", "mark.smith@example.com", "smithm1005", "HardPassword2",
                "XXAAAABBBBCCCCDDDDEEEE0002", "100.00", "321 Side Street, Katowice, Silesia, 00-002, Poland",
                "XXXXYYYYZZZZ0002", "XYZ00002");

        logger.info(userMapper.map(userRepository.save(user1)).toString());
        logger.info(userMapper.map(userRepository.save(user2)).toString());
    }

    private User createUser(String name, String surname, String email, String username, String passwordStr,
                            String accountNumber, String balance,
                            String address, String cardNumber, String identification) {

        Password password = new Password(null, passwordEncoder.encode(passwordStr));
        User user = new User(null, name, surname, email, username, password,
                null, null, null, UserStatus.ACTIVE, 0);
        Account account = new Account(null, accountNumber, user, new BigDecimal(balance));
        PersonalData personalData = new PersonalData(null, user, address, cardNumber, identification);

        user.setAccount(account);
        user.setPersonalData(personalData);

        return user;
    }
}
