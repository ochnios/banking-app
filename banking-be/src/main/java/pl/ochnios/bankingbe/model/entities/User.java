package pl.ochnios.bankingbe.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

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
}
