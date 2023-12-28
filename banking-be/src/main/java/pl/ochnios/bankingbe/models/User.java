package pl.ochnios.bankingbe.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "account_id")
    private Account account;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_data_id", referencedColumnName = "personal_data_id")
    private PersonalData personalData;

    // Technical attributes
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private Integer loginAttempts;
}
