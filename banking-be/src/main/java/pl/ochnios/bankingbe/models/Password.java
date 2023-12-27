package pl.ochnios.bankingbe.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "passwords")
public class Password {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "password_id")
    private UUID id;

    @NotNull
    private String password;
}
