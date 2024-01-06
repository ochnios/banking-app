package pl.ochnios.bankingbe.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.ochnios.bankingbe.exceptions.PasswordValidationException;
import pl.ochnios.bankingbe.model.entities.Password;
import pl.ochnios.bankingbe.model.entities.User;
import pl.ochnios.bankingbe.repositories.UserRepository;
import pl.ochnios.bankingbe.security.SecretShare;
import pl.ochnios.bankingbe.security.Shamir;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private static final int SECRET_LENGTH = 64;
    private static final int TOTAL_SHARES = 16;
    private static final int MINIMUM_SHARES = 5;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void setUserPassword(String userId, String inputPassword) {
        if (!isPasswordValid(inputPassword)) {
            throw new PasswordValidationException("Entered password does not met the requirements");
        }
        User user = findUserById(userId);
        user.setPassword(buildPartialPassword(inputPassword));
        userRepository.save(user);
    }

    public boolean verifyUserPassword(String userId, String partialPassword) {
        if (!isPartialPasswordValid(partialPassword)) {
            return false;
        }

        Password password = findUserById(userId).getPasswordEntity();
        SecretShare[] selectedShares = selectedSharesFromPersist(password.getShares(), password.getCurrentPositions());
        BigInteger recoveredSecret = Shamir.combine(selectedShares, partialPassword);

        return passwordEncoder.matches(recoveredSecret.toString(), password.getSecretHash());
    }

    // TEMP: PUBLIC ONLY FOR DATA SEEDER!!!
    public Password buildPartialPassword(String inputPassword) {
        if (!isPasswordValid(inputPassword)) {
            throw new PasswordValidationException("Entered password does not met the requirements");
        }

        Random random = getSecureRandom();
        BigInteger secret = Shamir.generateSecret(SECRET_LENGTH, random);
        SecretShare[] shares = Shamir.split(secret, inputPassword, MINIMUM_SHARES, TOTAL_SHARES, random);

        int[] positions = {0, 1, 2, 3, 4}; // TEMP

        Password password = new Password();
        password.setHash(passwordEncoder.encode(inputPassword));
        password.setSecretHash(passwordEncoder.encode(secret.toString()));
        password.setCurrentPositions(positionsToPersist(positions));
        password.setShares(sharesToPersist(shares));

        return password;
    }

    private User findUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%s not found", userId)));
    }

    private String sharesToPersist(SecretShare[] shares) {
        return Arrays.stream(shares)
                .map(share -> share.share().toString())
                .collect(Collectors.joining(","));
    }

    private String positionsToPersist(int[] positions) {
        return Arrays.stream(positions)
                .boxed()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private SecretShare[] selectedSharesFromPersist(String shares, String positions) {
        String[] sharesArr = shares.split(",");
        String[] positionsArr = positions.split(",");
        SecretShare[] recovered = new SecretShare[positionsArr.length];
        for (int i = 0; i < positionsArr.length; i++) {
            int pos = Integer.parseInt(positionsArr[i]);
            recovered[i] = new SecretShare(pos + 1, new BigInteger(sharesArr[pos]));
        }
        return recovered;
    }

    private boolean isPasswordValid(String pwd) {
        return pwd != null && pwd.length() == TOTAL_SHARES
                && hasAllowedCharactersOnly(pwd) && isSecureEnough(pwd);
    }

    private boolean isPartialPasswordValid(String pwd) {
        return pwd != null && pwd.length() == MINIMUM_SHARES
                && hasAllowedCharactersOnly(pwd);
    }

    private boolean hasAllowedCharactersOnly(String pwd) {
        for (char c : pwd.toCharArray()) {
            if (c <= 0x20 || c >= 0x7F) {
                return false;
            }
        }
        return true;
    }

    public boolean isSecureEnough(String pwd) {
        boolean hasLowercase = false; // [a-z]
        boolean hasUppercase = false; // [A-Z]
        boolean hasDigit = false; // [0-9]
        boolean hasSpecial = false; // [!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~]
        for (char c : pwd.toCharArray()) {
            if (c >= 'a' && c <= 'z') hasLowercase = true;
            else if (c >= 'A' && c <= 'Z') hasUppercase = true;
            else if (c >= '0' && c <= '9') hasDigit = true;
            else hasSpecial = true;
        }

        return hasLowercase && hasUppercase && hasDigit && hasSpecial;
    }

    private SecureRandom getSecureRandom() {
        try {
            return SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            return new SecureRandom();
        }
    }
}
