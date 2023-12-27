package pl.ochnios.bankingbe.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "account_id")
    private UUID id;

    @NotNull
    @Size(max = 34)
    @Column(unique = true, nullable = false)
    private String iban;

    @NotNull
    @OneToOne(mappedBy = "account")
    private User owner;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Transfer> transfers;
}
