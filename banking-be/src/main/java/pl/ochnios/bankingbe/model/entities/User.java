package pl.ochnios.bankingbe.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
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
    @Column(unique = true, nullable = false)
    private String username;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "password_id", referencedColumnName = "password_id")
    private Password password;

    // In real application there should be OneToMany relationship
    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Account account;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PersonalData personalData;

    @OneToMany
    @JoinColumn(name = "user_id")
    private List<Transfer> transfers;

    // Technical attributes
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private Integer loginAttempts;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<SimpleGrantedAuthority>();
    }

    @Override
    public String getPassword() {
        return password.getHash();
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
}
