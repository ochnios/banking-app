package pl.ochnios.bankingbe.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(nullable = false)
    private String surname;

    @NotNull
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_.-]{3,20}$")
    @Column(unique = true, nullable = false)
    private String username;

    @Setter
    @NotNull
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "password_id")
    private Password password;

    @Setter
    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Setter
    @NotNull
    private Integer loginAttempts;

    @Setter
    @NotNull
    private Integer changeAttempts;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<SimpleGrantedAuthority>();
    }

    @Override
    public String getPassword() {
        return password.getHash();
    }

    public Password getPasswordEntity() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.BLOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status != UserStatus.INACTIVE;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User other = (User) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }

    public static boolean isUsernameCorrect(String username) {
        if (username != null && username.length() >= 5) {
            for (char c : username.toCharArray()) {
                if (c <= 0x20 || c >= 0x7F) return false;
            }
            return true;
        } else return false;
    }
}
