package pl.ochnios.bankingbe.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Setter
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
}
