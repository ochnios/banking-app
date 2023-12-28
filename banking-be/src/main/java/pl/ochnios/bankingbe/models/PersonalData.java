package pl.ochnios.bankingbe.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "personal_data")
public class PersonalData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "personal_data_id")
    private UUID id;

    @Size(min = 3, max = 200)
    @Column(nullable = false)
    private String address;

    @Size(min = 16, max = 16)
    private String cardNumber;

    @Size(max = 50)
    private String identification;
}
