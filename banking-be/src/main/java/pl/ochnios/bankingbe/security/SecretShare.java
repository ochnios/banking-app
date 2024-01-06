package pl.ochnios.bankingbe.security;

import java.math.BigInteger;

public record SecretShare(int number, BigInteger share) {
}
