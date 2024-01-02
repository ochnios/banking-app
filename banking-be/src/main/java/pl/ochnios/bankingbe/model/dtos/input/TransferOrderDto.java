package pl.ochnios.bankingbe.model.dtos.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.apache.commons.text.StringEscapeUtils;

@Getter
public class TransferOrderDto {

    @NotNull(message = "Title must not be null")
    @Size(min = 3, max = 80, message = "Title length must be between 3 and 80 characters")
    private final String title;

    @NotNull(message = "Amount must not be null")
    @Pattern(regexp = "^[0-9]{1,15}(\\.[0-9]{1,2})?$", message = "Invalid amount")
    private final String amount;

    @NotNull(message = "Recipient account number must not be null")
    @Size(min = 26, max = 26, message = "Recipient account number length must be exactly 26 characters")
    @Pattern(regexp = "^[0-9]{26}$", message = "Invalid recipient account number")
    private final String recipientAccountNumber;

    @NotNull(message = "Recipient name must not be null")
    @Size(min = 3, max = 101, message = "Recipient name must be between 3 and 101 characters")
    private final String recipientName;

    @Size(min = 3, max = 200, message = "Recipient address must be between 3 and 200 characters")
    private final String recipientAddress;

    public TransferOrderDto(String title, String amount, String recipientAccountNumber, String recipientName, String recipientAddress) {
        this.title = StringEscapeUtils.escapeJava(title);
        this.amount = StringEscapeUtils.escapeJava(amount);
        this.recipientAccountNumber = StringEscapeUtils.escapeJava(recipientAccountNumber);
        this.recipientName = StringEscapeUtils.escapeJava(recipientName);
        this.recipientAddress = StringEscapeUtils.escapeJava(recipientAddress);
    }
}
