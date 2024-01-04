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

    @NotNull
    @Column(nullable = false)
    private String secret;

    @NotNull
    @Column(nullable = false)
    private String currentCombo;

    @NotNull
    @Column(nullable = false)
    private Integer s1;
    @NotNull
    @Column(nullable = false)
    private Integer s2;
    @NotNull
    @Column(nullable = false)
    private Integer s3;
    @NotNull
    @Column(nullable = false)
    private Integer s4;
    @NotNull
    @Column(nullable = false)
    private Integer s5;
    @NotNull
    @Column(nullable = false)
    private Integer s6;
    @NotNull
    @Column(nullable = false)
    private Integer s7;
    @NotNull
    @Column(nullable = false)
    private Integer s8;
    @NotNull
    @Column(nullable = false)
    private Integer s9;
    @NotNull
    @Column(nullable = false)
    private Integer s10;
    @NotNull
    @Column(nullable = false)
    private Integer s11;
    @NotNull
    @Column(nullable = false)
    private Integer s12;
    @NotNull
    @Column(nullable = false)
    private Integer s13;
    @NotNull
    @Column(nullable = false)
    private Integer s14;
    @NotNull
    @Column(nullable = false)
    private Integer s15;
    @NotNull
    @Column(nullable = false)
    private Integer s16;

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
