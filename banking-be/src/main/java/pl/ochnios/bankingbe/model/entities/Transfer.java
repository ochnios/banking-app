package pl.ochnios.bankingbe.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transfer_id")
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private Date time;

    @NotNull
    @Size(min = 3, max = 80)
    @Column(nullable = false)
    private String title;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @NotNull
    @Size(min = 26, max = 26)
    @Column(nullable = false, length = 26)
    private String senderAccountNumber;

    @NotNull
    @Size(min = 3, max = 80)
    @Column(nullable = false)
    private String senderName;

    @NotNull
    @Size(min = 3, max = 80)
    @Column(nullable = false)
    private String senderAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    private User recipient;

    @NotNull
    @Size(min = 26, max = 26)
    @Column(nullable = false, length = 26)
    private String recipientAccountNumber;

    @NotNull
    @Size(min = 3, max = 80)
    @Column(nullable = false)
    private String recipientName;

    @NotNull
    @Size(min = 3, max = 80)
    @Column(nullable = false)
    private String recipientAddress;
}
