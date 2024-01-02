package pl.ochnios.bankingbe.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "personal_data")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "personal_data_id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_data_id")
    @MapsId
    private User user;

    @Size(min = 3, max = 200)
    @Column(nullable = false)
    private String address;

    @Size(min = 16, max = 16)
    private String cardNumber;

    @Size(max = 50)
    private String identification;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonalData other = (PersonalData) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }
}
