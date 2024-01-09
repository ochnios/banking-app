package pl.ochnios.bankingbe.exceptions;

public class BlockedAccountException extends RuntimeException {

    public BlockedAccountException(String message) {
        super(message);
    }
}
