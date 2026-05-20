

public class CheckingAccount extends Account {


    public CheckingAccount(String accountNumber, Customer owner) {
        super(accountNumber, owner);
    }

    @Override
    public boolean withdraw(double amount, String transactionId) {
        if (getBalance() >= amount) {
            setBalance(getBalance() - amount);
            System.out.println("Withdrawal successful — " + amount + " was withdrawn.");
            Transaction t = new Transaction(transactionId, amount, "Withdrawal", this, null, "COMPLETED");
            getTransactions().add(t);
            return true;
        } else {
            System.out.println("Insufficient funds — withdrawal failed.");
            return false;
        }
    }
}
