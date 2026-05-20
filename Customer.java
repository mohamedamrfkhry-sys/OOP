import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private String address;
    private ArrayList<Account> accounts;

    public Customer(String userId, String userName, String password, String name, String email, String Address) {
        super(userId, userName, password, name, email);
        this.address = Address;
        accounts = new ArrayList<>();
    }

    public List<Account> getAccounts() {return accounts;}
    public String getAddress() {return address;}
    public void setAddress(String a) {this.address = a;}

    public void openAccount(Account account) {
        if (account != null) {
            accounts.add(account);
            System.out.println("Account: " + account.getAccountNumber() + " successfully added to your profile.");
        }
    }

    public void viewAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("You have no accounts yet.");
            return;
        }
        System.out.println("Your Accounts Data: ");
        for (Account acc : accounts) {
            System.out.println("Account Number: " + acc.getAccountNumber() + " ,Balance: " + acc.getBalance() + " ,Status: " + acc.getStatus());
        }
    }

    public boolean makeTransaction(Account fromAccount, String Type, double amount, Account towardAccount, String transactionId) {
        if (!accounts.contains(fromAccount)) {
            System.out.println("You can't access this account.");
            return false;
        }
        if (Type.equalsIgnoreCase("Deposit")) {
            fromAccount.deposit(amount,transactionId);
            return true;

        } else if (Type.equalsIgnoreCase("Withdraw")) {
            return fromAccount.withdraw(amount, transactionId);

        } else if (Type.equalsIgnoreCase("Transfer") && towardAccount != null) {
            return fromAccount.transfer(transactionId, amount, towardAccount);

        } else {
            System.out.println("Either didn't enter Transaction type or amount of needed transaction");
            System.out.println("Try again please");
            return false;
        }
    }
}