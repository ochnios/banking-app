package pl.ochnios.bankingbe.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "passwords")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Password {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "password_id")
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private String hash;

    @NotNull
    @Column(nullable = false)
    private String secretHash;

    @NotNull
    @Column(nullable = false)
    private String currentPositions;

    @NotNull
    @Column(nullable = false, length = 500)
    private String shares;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password other = (Password) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }
}
