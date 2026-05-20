import java.util.ArrayList;
import java.util.List;

public class Bank {

    private List<User> users;
    private List<Account> accounts;
    private final List<Transaction> transactions;
    private final FileManager fileManager;

    public Bank() {
        this.users = new ArrayList<>();
        this.accounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.fileManager = new FileManager();

        this.users = fileManager.loadUsers();
        this.accounts = fileManager.loadAccounts(this.users);
    }

    public List<User> getUsers() { return users; }
    public List<Account> getAccounts() { return accounts; }
    public List<Transaction> getTransactions() { return transactions; }

    public void createUser(User user) {
        if (user != null) {
            users.add(user);
            System.out.println(user.getName() + " has been added to the bank's user database.");
        }
    }


    public User findUser(String userName) {
        for (User user : users) {
            if (user.getUserName().equalsIgnoreCase(userName)) {
                return user;
            }
        }
        System.out.println(userName + "' not found in the system.");
        return null;
    }


    public Account findAccount(String accountNumber) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(accountNumber)) {
                return acc;
            }
        }
        return null;
    }

    public void createAccount(Account account, Customer customer) {
        if (account != null && customer != null) {
            this.accounts.add(account);
            customer.openAccount(account);
        }
    }

    public boolean processTransaction(String fromAccountNumber, String type, double amount, String towardAccountNumber, String transactionId) {

        Account from = findAccount(fromAccountNumber);
        if (from == null) {
            System.out.println("The Main account does not exist.");
            return false;
        }

        boolean success = false;
        switch (type.toLowerCase()) {
            case "deposit":
                from.deposit(amount, transactionId);
                success = true;
                break;

            case "withdrawal":
                success = from.withdraw(amount, transactionId);
                break;

            case "transfer":
                Account toward = findAccount(towardAccountNumber);
                if (toward != null) {
                    success = from.transfer(transactionId, amount, toward);
                } else {
                    System.out.println("The account in which the transaction to; does not exist.");
                }
                break;

            default:
                System.out.println("Transaction type error");
        }

        if (success) {
            List<Transaction> history = from.getTransactions();
            Transaction newReceipt = history.get(history.size() - 1);

            this.transactions.add(newReceipt);

            fileManager.recordTransaction(newReceipt);
        }

        return success;
    }

    public void saveData() {
        fileManager.saveUsers(this.users);
        fileManager.saveAccounts(this.accounts);
    }
}