package pl.ochnios.bankingbe.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;


@Entity
@Table(name = "transfers")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transfer_id")
    private UUID id;

    @CreationTimestamp
    @Column(nullable = false)
    private Date time;

    @NotNull
    @Size(min = 3, max = 80)
    @Column(nullable = false)
    private String title;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @Setter
    @NotNull
    @Size(min = 26, max = 26)
    @Pattern(regexp = "^[0-9]{26}$")
    @Column(nullable = false, length = 26)
    private String senderAccountNumber;

    @Setter
    @NotNull
    @Size(min = 3, max = 101)
    @Column(nullable = false, length = 101)
    private String senderName;

    @Setter
    @Size(min = 3, max = 200)
    @Column(length = 200)
    private String senderAddress;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private User recipient;

    @NotNull
    @Size(min = 26, max = 26)
    @Pattern(regexp = "^[0-9]{26}$")
    @Column(nullable = false, length = 26)
    private String recipientAccountNumber;

    @NotNull
    @Size(min = 3, max = 101)
    @Column(nullable = false, length = 101)
    private String recipientName;

    @Size(min = 3, max = 200)
    @Column(length = 200)
    private String recipientAddress;

    @Setter
    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private TransferType type;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer other = (Transfer) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }
}
