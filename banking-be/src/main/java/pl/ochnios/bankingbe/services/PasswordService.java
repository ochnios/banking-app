package pl.ochnios.bankingbe.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.ochnios.bankingbe.exceptions.PasswordValidationException;
import pl.ochnios.bankingbe.model.entities.Password;
import pl.ochnios.bankingbe.repositories.UserRepository;
import pl.ochnios.bankingbe.security.SecretShare;
import pl.ochnios.bankingbe.security.Shamir;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private static final int SECRET_LENGTH = 64;
    private static final int TOTAL_SHARES = 16;
    private static final int MINIMUM_SHARES = 5;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Password cratePartialPassword(String inputPassword) {
        if (!isPasswordValid(inputPassword)) {
            throw new PasswordValidationException("Entered password does not met the requirements");
        }
        return buildPartialPassword(inputPassword);
    }

    public boolean verifyPartialPassword(Password password, String inputPassword) {
        if (!isPartialPasswordValid(inputPassword)) {
            return false;
        }

        SecretShare[] selectedShares = selectedSharesFromPersist(password.getShares(), password.getCurrentPositions());
        BigInteger recoveredSecret = Shamir.combine(selectedShares, inputPassword);

        return passwordEncoder.matches(recoveredSecret.toString(), password.getSecretHash());
    }

    public void resetPositions(Password password) {
        String newPositions;
        do {
            newPositions = positionsToPersist(generatePositions());
        } while (newPositions.equals(password.getCurrentPositions()));
        password.setCurrentPositions(newPositions);
    }

    private Password buildPartialPassword(String inputPassword) {
        Random random = getSecureRandom();
        BigInteger secret = Shamir.generateSecret(SECRET_LENGTH, random);
        SecretShare[] shares = Shamir.split(secret, inputPassword, MINIMUM_SHARES, TOTAL_SHARES, random);

        Password password = new Password();
        password.setHash(passwordEncoder.encode(inputPassword));
        password.setSecretHash(passwordEncoder.encode(secret.toString()));
        password.setCurrentPositions(positionsToPersist(generatePositions()));
        password.setShares(sharesToPersist(shares));

        return password;
    }

    private int[] generatePositions() {
        Random random = new Random();
        Set<Integer> positionsSet = new HashSet<>();
        while (positionsSet.size() < MINIMUM_SHARES) {
            positionsSet.add(random.nextInt(TOTAL_SHARES) + 1);
        }
        int[] positionsArray = positionsSet.stream().mapToInt(Integer::intValue).toArray();
        Arrays.sort(positionsArray);
        return positionsArray;
    }

    private String positionsToPersist(int[] positions) {
        return Arrays.stream(positions)
                .boxed()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private String sharesToPersist(SecretShare[] shares) {
        return Arrays.stream(shares)
                .map(share -> share.share().toString())
                .collect(Collectors.joining(","));
    }

    private SecretShare[] selectedSharesFromPersist(String shares, String positions) {
        String[] sharesArr = shares.split(",");
        String[] positionsArr = positions.split(",");
        SecretShare[] selected = new SecretShare[positionsArr.length];
        for (int i = 0; i < positionsArr.length; i++) {
            int pos = Integer.parseInt(positionsArr[i]);
            selected[i] = new SecretShare(pos, new BigInteger(sharesArr[pos - 1]));
        }
        return selected;
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
