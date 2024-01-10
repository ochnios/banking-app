package pl.ochnios.bankingbe.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.ochnios.bankingbe.exceptions.BlockedAccountException;
import pl.ochnios.bankingbe.model.dtos.output.UserDto;
import pl.ochnios.bankingbe.model.entities.User;
import pl.ochnios.bankingbe.model.mappers.UserMapper;
import pl.ochnios.bankingbe.repositories.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

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

    private boolean isAccountActive(User user) {
        return user.isAccountNonLocked() && user.isEnabled();
    }
}
