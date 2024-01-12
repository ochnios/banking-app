package pl.ochnios.bankingbe.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.ochnios.bankingbe.exceptions.BlockedAccountException;
import pl.ochnios.bankingbe.exceptions.ResetTokenValidationException;
import pl.ochnios.bankingbe.model.dtos.input.ChangePasswordDto;
import pl.ochnios.bankingbe.model.dtos.input.NewPasswordDto;
import pl.ochnios.bankingbe.model.dtos.output.UserDto;
import pl.ochnios.bankingbe.model.entities.Password;
import pl.ochnios.bankingbe.model.entities.User;
import pl.ochnios.bankingbe.model.entities.UserStatus;
import pl.ochnios.bankingbe.model.mappers.UserMapper;
import pl.ochnios.bankingbe.repositories.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordService passwordService;

    public UserDto getUserById(String userId) {
        return userMapper.map(getUserEntityById(userId));
    }

    public UserDto getUserByUsername(String username) {
        return userMapper.map(getUserEntityByUsername(username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username=%s not found", username)));
    }

    public String generateResetPasswordToken(String username) {
        User user = getUserEntityByUsername(username);
        Password password = user.getPasswordEntity();
        if (passwordService.isResetTokenValid(password)) {
            throw new IllegalStateException(String.format("Active reset token already exists for username=%s", username));
        } else {
            passwordService.setResetToken(password);
        }
        saveUser(user);
        return password.getResetToken().toString();
    }

    public void resetUserPassword(String token, NewPasswordDto newPasswordDto) {
        User user = getUserByToken(token);
        validateUserAccount(user);
        validateResetToken(user);
        user.setPassword(passwordService.resetPassword(newPasswordDto));
        saveUser(user);
    }

    public void changeUserPassword(ChangePasswordDto changePasswordDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (passwordService.passwordsMatches(changePasswordDto.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordService.resetPassword(changePasswordDto.getNewPassword()));
            user.setChangeAttempts(0);
            saveUser(user);
        } else {
            handleFailedChangeAttempt(user);
            throw new BadCredentialsException("Entered current password is incorrect");
        }
    }

    protected User getUserEntityById(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%s not found", userId)));

        if (!isAccountActive(user))
            throw new BlockedAccountException(String.format("User with id=%s is blocked", userId));

        return user;
    }

    protected User getUserEntityByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with username=%s not found", username)));

        if (!isAccountActive(user))
            throw new BlockedAccountException(String.format("User with username=%s is blocked", username));

        return user;
    }

    protected User saveUser(User user) {
        return userRepository.save(user);
    }

    private void handleFailedChangeAttempt(User user) {
        if (user.getChangeAttempts() >= 3) {
            user.setStatus(UserStatus.BLOCKED);
            saveUser(user);
            throw new BlockedAccountException("Account blocked after 3 failed change password attempts.");
        }
        user.setChangeAttempts(user.getChangeAttempts() + 1);
        saveUser(user);
    }

    private User getUserByToken(String token) {
        return userRepository.findByPassword_ResetToken(UUID.fromString(token))
                .orElseThrow(() -> new ResetTokenValidationException("Given reset token is not valid"));
    }

    private void validateUserAccount(User user) {
        if (!user.isAccountNonLocked()) {
            throw new BlockedAccountException("User account for given token is blocked.");
        }
    }

    private void validateResetToken(User user) {
        if (!passwordService.isResetTokenValid(user.getPasswordEntity())) {
            throw new ResetTokenValidationException("Given reset token expired");
        }
    }

    private boolean isAccountActive(User user) {
        return user.isAccountNonLocked() && user.isEnabled();
    }
}
