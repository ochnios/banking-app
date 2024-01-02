package pl.ochnios.bankingbe.exceptions;

public class TransferFailureException extends RuntimeException {
    
    public TransferFailureException(String message) {
        super(message);
    }
}
