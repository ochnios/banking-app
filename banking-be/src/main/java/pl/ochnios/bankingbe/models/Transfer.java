package pl.ochnios.bankingbe.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transfer_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Account sender;

    @NotNull
    @Size(max = 34)
    private String recipientIban;

    @NotNull
    @Size(min = 3, max = 80)
    private String recipientName;

    @NotNull
    @Size(min = 3, max = 80)
    private String recipientAddress;

    @NotNull
    @Size(min = 3, max = 80)
    private String title;

    @NotNull
    private Double amount;
}
