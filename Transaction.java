import java.time.LocalDateTime;

public class Transaction {
    private final String transactionId;
    private final LocalDateTime timestamp;
    private final double amount;
    private final String type;
    private final Account fromAccount;
    private final Account towardAccount;
    private final String status;

    public Transaction(String transactionId, double amount, String type, Account fromAccount, Account towardAccount, String status) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.type = type;
        this.fromAccount = fromAccount;
        this.towardAccount = towardAccount;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getTransactionId() { return transactionId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public Account getFromAccount() { return fromAccount; }
    public Account gettowardAccount() { return towardAccount; }
    public String getStatus() { return status; }


    public String getTransactionDetails() {
        String details = "Transaction ID: " + this.transactionId +
                " ,Type: " + this.type + " ,Amount: " + this.amount + " ,Status: " + this.status;

        if (this.towardAccount != null) {
            details += " ,Transferred To: " + this.towardAccount.getAccountNumber();
        }
        return details;
    }
}