package pl.ochnios.bankingbe.model.dtos.output;

import lombok.Data;

@Data
public class TransferDto {

    private String id;
    private String time;
    private String title;
    private String amount;
    private String senderAccountNumber;
    private String senderName;
    private String senderAddress;
    private String recipientAccountNumber;
    private String recipientName;
    private String recipientAddress;
    private String type;
}
