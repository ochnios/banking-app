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
    private String name;

    @NotNull
    @Size(min = 3, max = 50)
    private String surname;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 3, max = 200)
    private String address;

    @Size(min = 16, max = 16)
    private String cardNumber; // sensitive data

    @Size(max = 50)
    private String identification; // sensitive data

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "password_id", referencedColumnName = "password_id")
    private Password password;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "account_id")
    private Account account;

    private UserStatus status;

    private Integer loginAttempts;
}
