import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileManager {

    private final String USERS_FILE        = "users.txt";
    private final String ACCOUNTS_FILE     = "accounts.txt";
    private final String TRANSACTIONS_FILE = "transactions.txt";


    public void saveUsers(List<User> users) {
        try {
            PrintWriter writer = new PrintWriter(new File(USERS_FILE));

            for (User user : users) {

                if (user instanceof Admin) {
                    Admin a = (Admin) user;
                    writer.println(
                            "ADMIN"                  + "," +
                                    a.getUserId()            + "," +
                                    a.getUserName()          + "," +
                                    a.getPassword()          + "," +
                                    a.getName()              + "," +
                                    a.getEmail()             + "," +
                                    a.getEmployeeId()        + "," +
                                    a.getPosition()          + "," +
                                    a.getSecurityClearance() + "," +
                                    a.getAdminPrivileges()
                    );

                } else if (user instanceof Employee) {
                    Employee e = (Employee) user;
                    writer.println(
                            "EMPLOYEE"         + "," +
                                    e.getUserId()      + "," +
                                    e.getUserName()    + "," +
                                    e.getPassword()    + "," +
                                    e.getName()        + "," +
                                    e.getEmail()       + "," +
                                    e.getEmployeeId()  + "," +
                                    e.getPosition()
                    );

                } else if (user instanceof Customer) {
                    Customer c = (Customer) user;
                    writer.println(
                            "CUSTOMER"       + "," +
                                    c.getUserId()    + "," +
                                    c.getUserName()  + "," +
                                    c.getPassword()  + "," +
                                    c.getName()      + "," +
                                    c.getEmail()     + "," +
                                    c.getAddress()   + "," +
                                    "NONE"
                    );
                }
            }

            writer.close();
            System.out.println("Users saved successfully to " + USERS_FILE);

        } catch (FileNotFoundException e) {
            System.out.println("ERROR - could not save users: " + e.getMessage());
        }
    }



    public List<User> loadUsers() {
        List<User> loadedUsers = new ArrayList<>();

        try {
            File file = new File(USERS_FILE);
            if (!file.exists()) {
                System.out.println("users.txt not found - starting with empty user list.");
                return loadedUsers;
            }

            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] data = line.split(",");
                String role = data[0];

                if (role.equals("ADMIN") && data.length == 10) {

                    Admin admin = new Admin(
                            data[1],
                            data[2],
                            data[3],
                            data[4],
                            data[5],
                            data[6],
                            data[7],
                            Integer.parseInt(data[8]),
                            data[9]
                    );
                    loadedUsers.add(admin);

                } else if (role.equals("EMPLOYEE") && data.length == 8) {
                    // EMPLOYEE,userId,userName,password,name,email,employeeId,position
                    Employee employee = new Employee(
                            data[1],
                            data[2],
                            data[3],
                            data[4],
                            data[5],
                            data[6],
                            data[7]
                    );
                    loadedUsers.add(employee);

                } else if (role.equals("CUSTOMER") && data.length == 8) {
                    // CUSTOMER,userId,userName,password,name,email,address,NONE
                    Customer customer = new Customer(
                            data[1],
                            data[2],
                            data[3],
                            data[4],
                            data[5],
                            data[6]
                    );
                    loadedUsers.add(customer);

                } else {
                    System.out.println("WARNING - skipping unrecognised line: " + line);
                }
            }

            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR - could not load users: " + e.getMessage());
        }

        return loadedUsers;
    }


    public void saveAccounts(List<Account> accounts) {
        try {
            PrintWriter writer = new PrintWriter(new File(ACCOUNTS_FILE));

            for (Account acc : accounts) {

                if (acc instanceof SavingsAccount) {
                    SavingsAccount sa = (SavingsAccount) acc;
                    writer.println(
                            "SAVINGS"                 + "," +
                                    sa.getAccountNumber()     + "," +
                                    sa.getBalance()           + "," +
                                    sa.getStatus()            + "," +
                                    sa.getOwner().getUserId() + "," +
                                    sa.getInterestRate()      + "," +
                                    sa.getMinimumBalance()    + "," +
                                    sa.getWithdrawalLimit()
                    );

                } else {
                    writer.println(
                            "CHECKING"                 + "," +
                                    acc.getAccountNumber()     + "," +
                                    acc.getBalance()           + "," +
                                    acc.getStatus()            + "," +
                                    acc.getOwner().getUserId()
                    );
                }
            }

            writer.close();
            System.out.println("Accounts saved successfully to " + ACCOUNTS_FILE);

        } catch (FileNotFoundException e) {
            System.out.println("ERROR - could not save accounts: " + e.getMessage());
        }
    }



    public List<Account> loadAccounts(List<User> existingUsers) {
        List<Account> loadedAccounts = new ArrayList<>();

        try {
            File file = new File(ACCOUNTS_FILE);
            if (!file.exists()) {
                System.out.println("accounts.txt not found - starting with empty account list.");
                return loadedAccounts;
            }

            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] data = line.split(",");
                String type = data[0];

                if (type.equals("CHECKING") && data.length == 5) {
                    // CHECKING,accountNumber,balance,status,ownerId
                    String accNumber = data[1];
                    double balance   = Double.parseDouble(data[2]);
                    String status    = data[3];
                    String ownerId   = data[4];

                    Customer owner = findCustomerById(ownerId, existingUsers);
                    if (owner == null) {
                        System.out.println("WARNING - no customer found for id: " + ownerId);
                        continue;
                    }

                    CheckingAccount ca = new CheckingAccount(accNumber, owner);
                    ca.setBalance(balance);
                    ca.setStatus(status);
                    owner.getAccounts().add(ca);
                    loadedAccounts.add(ca);

                } else if (type.equals("SAVINGS") && data.length == 8) {

                    String accNumber    = data[1];
                    double balance      = Double.parseDouble(data[2]);
                    String status       = data[3];
                    String ownerId      = data[4];
                    double interestRate = Double.parseDouble(data[5]);
                    double minBalance   = Double.parseDouble(data[6]);
                    double wdLimit      = Double.parseDouble(data[7]);

                    Customer owner = findCustomerById(ownerId, existingUsers);
                    if (owner == null) {
                        System.out.println("WARNING - no customer found for id: " + ownerId);
                        continue;
                    }

                    SavingsAccount sa = new SavingsAccount(accNumber, owner);
                    sa.setBalance(balance);
                    sa.setStatus(status);
                    sa.setInterestRate(interestRate);
                    sa.setMinimumBalance(minBalance);
                    sa.setWithdrawalLimit((int) wdLimit);
                    owner.getAccounts().add(sa);
                    loadedAccounts.add(sa);

                } else {
                    System.out.println("WARNING - skipping unrecognised line: " + line);
                }
            }

            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR - could not load accounts: " + e.getMessage());
        }

        return loadedAccounts;
    }



    public void recordTransaction(Transaction transaction) {
        try {
            FileWriter fw = new FileWriter(TRANSACTIONS_FILE, true); // append mode

            String fromAcc   = (transaction.getFromAccount()   != null)
                    ? transaction.getFromAccount().getAccountNumber()
                    : "NULL";
            String towardAcc = (transaction.gettowardAccount() != null)
                    ? transaction.gettowardAccount().getAccountNumber()
                    : "NULL";

            fw.write(
                    transaction.getTransactionId() + "," +
                            transaction.getTimestamp()     + "," +
                            transaction.getType()          + "," +
                            transaction.getAmount()        + "," +
                            fromAcc                        + "," +
                            towardAcc                      + "," +
                            transaction.getStatus()        + "\n"
            );

            fw.close();

        } catch (IOException e) {
            System.out.println("ERROR - could not record transaction: " + e.getMessage());
        }
    }

    private Customer findCustomerById(String userId, List<User> existingUsers) {
        for (User u : existingUsers) {
            if (u.getUserId().equals(userId) && u instanceof Customer) {
                return (Customer) u;
            }
        }
        return null;
    }
}
