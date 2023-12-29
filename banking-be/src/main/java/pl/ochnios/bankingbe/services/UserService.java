package pl.ochnios.bankingbe.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.ochnios.bankingbe.model.entities.User;
import pl.ochnios.bankingbe.repositories.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public User registerUser(User user) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(@NotBlank String id) {
        return userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%s not found", id)));
    }

    public User getUserByUsername(@NotBlank String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with username=%s not found", username)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.getUserByUsername(username);
    }
}
