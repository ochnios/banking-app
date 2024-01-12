package pl.ochnios.bankingbe.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.ochnios.bankingbe.exceptions.PasswordValidationException;
import pl.ochnios.bankingbe.model.dtos.input.NewPasswordDto;
import pl.ochnios.bankingbe.model.entities.Password;
import pl.ochnios.bankingbe.security.SecretShare;
import pl.ochnios.bankingbe.security.Shamir;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private static final int SECRET_LENGTH = 64;
    private static final int MINIMUM_SHARES = 5;
    private static final int MIN_PASSWORD_LEN = 12;
    private static final int MAX_PASSWORD_LEN = 24;
    private static final int MINUTE = 60 * 1000;

    private final PasswordEncoder passwordEncoder;

    public Password cratePartialPassword(String inputPassword) {
        if (!isPasswordValid(inputPassword)) {
            throw new PasswordValidationException("Entered password does not met the requirements");
        }
        return buildPartialPassword(inputPassword);
    }

    protected Password resetPassword(NewPasswordDto newPasswordDto) {
        if (!newPasswordDto.getPassword().equals(newPasswordDto.getPasswordRetyped())) {
            throw new PasswordValidationException("Passwords are not the same");
        }
        return cratePartialPassword(newPasswordDto.getPassword());
    }

    protected boolean passwordsMatches(String provided, String hash) {
        return passwordEncoder.matches(provided, hash);
    }

    protected boolean verifyPartialPassword(Password password, String inputPassword) {
        if (!isPartialPasswordValid(inputPassword)) {
            return false;
        }
        BigInteger recoveredSecret = Shamir.combine(password.getSharesForCurrentPositions(), inputPassword);
        return passwordEncoder.matches(recoveredSecret.toString(), password.getSecretHash());
    }

    protected void resetPositions(Password password) {
        int[] newPositions;
        do {
            newPositions = generatePositions(getSecureRandom(), password.getLength());
        } while (Arrays.equals(newPositions, password.getCurrentPositions()));
        password.setCurrentPositions(newPositions);
    }

    protected int[] fakePartialPasswordPositions(String username) {
        LocalDate date = LocalDate.now();
        Random fakeRandom = new Random(username.hashCode() + date.getDayOfMonth() * date.getMonthValue());
        return generatePositions(fakeRandom, MAX_PASSWORD_LEN);
    }

    protected boolean isResetTokenValid(Password password) {
        Date expires = password.getResetTokenExpiration();
        return expires != null && (new Date()).before(expires);
    }

    protected void setResetToken(Password password) {
        Date expires = new Date(new Date().getTime() + 15 * MINUTE);
        password.setResetToken(UUID.randomUUID());
        password.setResetTokenExpiration(expires);
    }

    private Password buildPartialPassword(String inputPassword) {
        Random random = getSecureRandom();
        BigInteger secret = Shamir.generateSecret(SECRET_LENGTH, random);
        SecretShare[] shares = Shamir.split(secret, inputPassword, MINIMUM_SHARES, inputPassword.length(), random);

        Password password = new Password();
        password.setHash(passwordEncoder.encode(inputPassword));
        password.setLength(inputPassword.length());
        password.setSecretHash(passwordEncoder.encode(secret.toString()));
        password.setCurrentPositions(generatePositions(random, inputPassword.length()));
        password.setShares(shares);

        return password;
    }

    private int[] generatePositions(Random random, int totalPositions) {
        Set<Integer> positionsSet = new HashSet<>();
        while (positionsSet.size() < MINIMUM_SHARES) {
            positionsSet.add(random.nextInt(totalPositions) + 1);
        }
        int[] positionsArray = positionsSet.stream().mapToInt(Integer::intValue).toArray();
        Arrays.sort(positionsArray);
        return positionsArray;
    }

    private boolean isPasswordValid(String pwd) {
        return pwd != null && pwd.length() >= MIN_PASSWORD_LEN && pwd.length() <= MAX_PASSWORD_LEN
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

    private boolean isSecureEnough(String pwd) {
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
