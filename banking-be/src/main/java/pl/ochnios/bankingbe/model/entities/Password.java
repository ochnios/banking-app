package pl.ochnios.bankingbe.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "passwords")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Password {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "password_id")
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private String hash;
}
