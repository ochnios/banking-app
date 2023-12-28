package pl.ochnios.bankingbe.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "account_id")
    private UUID id;

    @NotNull
    @Size(min = 26, max = 26) // polish bank account number
    @Column(unique = true, nullable = false, length = 26)
    private String accountNumber;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
}
