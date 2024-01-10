package pl.ochnios.bankingbe.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import pl.ochnios.bankingbe.security.SecretShare;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

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

    private UUID resetToken;

    private Date resetTokenExpiration;

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

    public int[] getCurrentPositions() {
        String[] positionsStrArr = currentPositions.split(",");
        int[] positionsArr = new int[positionsStrArr.length];
        for (int i = 0; i < positionsStrArr.length; i++) {
            positionsArr[i] = Integer.parseInt(positionsStrArr[i]);
        }
        return positionsArr;
    }

    public void setCurrentPositions(int[] positions) {
        currentPositions = Arrays.stream(positions)
                .boxed()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public SecretShare[] getSharesForCurrentPositions() {
        String[] sharesArr = shares.split(",");
        int[] positionsArr = getCurrentPositions();
        SecretShare[] selected = new SecretShare[positionsArr.length];
        for (int i = 0; i < positionsArr.length; i++) {
            int pos = positionsArr[i];
            selected[i] = new SecretShare(pos, new BigInteger(sharesArr[pos - 1]));
        }
        return selected;
    }

    public void setShares(SecretShare[] shares) {
        this.shares = Arrays.stream(shares)
                .map(share -> share.share().toString())
                .collect(Collectors.joining(","));
    }
}
