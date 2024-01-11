package pl.ochnios.bankingbe.security;

import java.math.BigInteger;
import java.util.Random;

/*
    Adapted from https://stackoverflow.com/questions/19327651/java-implementation-of-shamirs-secret-sharing
    Very helpful:
     https://magicofsecurity.com/partial-passwords-done-right/
     https://en.wikipedia.org/wiki/Shamir%27s_secret_sharing
 */

public final class Shamir {

    // prime number must be longer then secret number
    private static final BigInteger PRIME = new BigInteger("618970019642690137449562111"); // 2^89-1

    public static BigInteger generateSecret(int length, Random random) {
        return new BigInteger(length, random);
    }

    public static SecretShare[] split(final BigInteger secret, String password, int needed, int available, Random random) {
        final BigInteger[] coeff = new BigInteger[needed];
        coeff[0] = secret;
        for (int i = 1; i < needed; i++) {
            BigInteger r;
            do {
                r = new BigInteger(PRIME.bitLength() - 1, random);
            } while (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(PRIME) >= 0);
            coeff[i] = r;
        }

        final SecretShare[] shares = new SecretShare[available];
        for (int x = 1; x <= available; x++) {
            BigInteger accum = secret;
            for (int exp = 1; exp < needed; exp++) {
                accum = accum.add(coeff[exp].multiply(BigInteger.valueOf(x).pow(exp).mod(PRIME))).mod(PRIME);
            }
            BigInteger px = new BigInteger(String.valueOf((int) password.charAt(x - 1)));
            accum = accum.subtract(px);
            shares[x - 1] = new SecretShare(x, accum);
        }

        return shares;
    }

    public static BigInteger combine(final SecretShare[] shares, String password) {
        BigInteger accum = BigInteger.ZERO;

        for (int formula = 0; formula < shares.length; formula++) {
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int count = 0; count < shares.length; count++) {
                if (formula == count)
                    continue; // i != j

                int startPos = shares[formula].number();
                int nextPos = shares[count].number();

                numerator = numerator.multiply(BigInteger.valueOf(nextPos).negate()).mod(PRIME); // (numerator * - nextPos) % prime;
                denominator = denominator.multiply(BigInteger.valueOf(startPos - nextPos)).mod(PRIME); // (denominator * (startPos - nextPos)) % prime;
            }
            BigInteger pf = new BigInteger(String.valueOf((int) password.charAt(formula)));
            BigInteger value = shares[formula].share().add(pf);
            BigInteger tmp = value.multiply(numerator).multiply(modInverse(denominator));
            accum = PRIME.add(accum).add(tmp).mod(PRIME); //  (prime + accum + (value * numerator * modInverse(denominator))) % prime;
        }

        return accum;
    }

    private static BigInteger[] gcdD(BigInteger a, BigInteger b) {
        if (b.compareTo(BigInteger.ZERO) == 0)
            return new BigInteger[]{a, BigInteger.ONE, BigInteger.ZERO};
        else {
            BigInteger n = a.divide(b);
            BigInteger c = a.mod(b);
            BigInteger[] r = gcdD(b, c);
            return new BigInteger[]{r[0], r[2], r[1].subtract(r[2].multiply(n))};
        }
    }

    private static BigInteger modInverse(BigInteger k) {
        k = k.mod(Shamir.PRIME);
        BigInteger r = (k.compareTo(BigInteger.ZERO) == -1) ? (gcdD(Shamir.PRIME, k.negate())[2]).negate() : gcdD(Shamir.PRIME, k)[2];
        return Shamir.PRIME.add(r).mod(Shamir.PRIME);
    }
}