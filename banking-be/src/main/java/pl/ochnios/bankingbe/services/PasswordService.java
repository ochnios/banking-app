package pl.ochnios.bankingbe.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ochnios.bankingbe.exceptions.PasswordValidationException;
import pl.ochnios.bankingbe.repositories.PasswordRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private PasswordRepository passwordRepository;

    public void createPassword(String password) {
        if (!isPasswordValid(password)) {
            throw new PasswordValidationException("Entered password does not met the requirements");
        }

        // partial password implementation...
    }

    private boolean isPasswordValid(String pwd) {
        return isLongEnough(pwd) && isAsciiPrintable(pwd) && isEntropySufficient(pwd);
    }

    private boolean isLongEnough(String pwd) {
        return pwd != null && pwd.length() == 16;
    }

    private boolean isAsciiPrintable(String pwd) {
        for (char c : pwd.toCharArray()) {
            if (c < 0x20 || c >= 0x7F) {
                return false;
            }
        }
        return true;
    }

    private boolean isEntropySufficient(String pwd) {
        return calculateEntropy(pwd) > 4.0; // TEMP
    }

    public double calculateEntropy(String pwd) {
        if (pwd == null || pwd.isEmpty()) {
            return 0;
        }

        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : pwd.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }

        double entropy = 0.0;
        int length = pwd.length();
        for (char c : frequencyMap.keySet()) {
            double frequency = (double) frequencyMap.get(c) / length;
            entropy -= frequency * (Math.log(frequency) / Math.log(2));
        }

        return entropy;
    }
}
