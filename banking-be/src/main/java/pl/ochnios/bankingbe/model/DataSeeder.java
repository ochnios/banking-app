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
import pl.ochnios.bankingbe.model.mappers.TransferMapper;
import pl.ochnios.bankingbe.model.mappers.UserMapper;
import pl.ochnios.bankingbe.repositories.AccountRepository;
import pl.ochnios.bankingbe.repositories.PersonalDataRepository;
import pl.ochnios.bankingbe.repositories.TransferRepository;
import pl.ochnios.bankingbe.repositories.UserRepository;
import pl.ochnios.bankingbe.services.PasswordService;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final static Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PersonalDataRepository personalDataRepository;
    private final TransferRepository transferRepository;
    private final UserMapper userMapper;
    private final AccountMapper accountMapper;
    private final PersonalDataMapper personalDataMapper;
    private final TransferMapper transferMapper;
    private final PasswordService passwordService;

    @Override
    @Transactional
    public void run(String... args) {
        User user1 = createUser("John", "Doe", "john.doe@example.com", "doej3622", "HardPassword123!");
        createPersonalData(user1, "123 Main Street, Warsaw, Mazovia, 00-001, Poland", "XXXXYYYYZZZZ0001", "XYZ00001");
        createAccount(user1, "00111122223333444455550001", "1000.00");

        User user2 = createUser("Mark", "Smith", "mark.smith@example.com", "smithm1005", "HardPassword123@");
        createPersonalData(user2, "321 Side Street, Katowice, Silesia, 00-002, Poland", "XXXXYYYYZZZZ0002", "XYZ00002");
        createAccount(user2, "00111122223333444455550002", "100.00");

        // Only internal transfers
        createTransfer(user1, user2, "From John to Mark 1", "10.00");
        createTransfer(user1, user2, "From John to Mark 2", "20.00");
        createTransfer(user1, user2, "From John to Mark 3", "30.00");
        createTransfer(user2, user1, "From Mark to John 1 ", "40.00");
        createTransfer(user2, user1, "From Mark to John 2 ", "50.00");
        createTransfer(user2, user1, "From Mark to John 3", "60.00");
    }

    private User createUser(String name, String surname, String email, String username, String passwordStr) {

        Password password = passwordService.buildPartialPassword(passwordStr);
        User user = new User(null, name, surname, email, username, password, UserStatus.ACTIVE, 0);
        user = userRepository.save(user);
        logger.info(userMapper.map(user).toString());
        return user;
    }

    private void createAccount(User user, String accountNumber, String balance) {
        Account account = new Account(null, accountNumber, user, new BigDecimal(balance));
        accountRepository.save(account);
        logger.info(accountMapper.map(account).toString());
    }

    private void createPersonalData(User user, String address, String cardNumber, String identification) {
        PersonalData personalData = new PersonalData(null, user, address, cardNumber, identification);
        personalDataRepository.save(personalData);
        logger.info(personalDataMapper.map(personalData).toString());
    }

    private void createTransfer(User sender, User recipient, String title, String amount) {
        Transfer transfer = Transfer.builder()
                .title(title)
                .amount(new BigDecimal(amount))
                .sender(sender)
                .senderAccountNumber(accountRepository.findById(sender.getId()).get().getAccountNumber())
                .senderName(String.format("%s %s", sender.getName(), sender.getSurname()))
                .senderAddress(personalDataRepository.findById(sender.getId()).get().getAddress())
                .recipient(recipient)
                .recipientAccountNumber(accountRepository.findById(recipient.getId()).get().getAccountNumber())
                .recipientName(String.format("%s %s", recipient.getName(), recipient.getSurname()))
                .recipientAddress(personalDataRepository.findById(recipient.getId()).get().getAddress())
                .type(TransferType.INTERNAL)
                .build();

        transferRepository.save(transfer);
        logger.info(transferMapper.map(transfer).toString());
    }
}
