public class Admin extends Employee {

    private final int securityClearance;
    private final String adminPrivileges;


    public Admin(String userId, String username, String password, String name, String email,
                 String employeeId, String position, int securityClearance, String adminPrivileges) {
        super(userId, username, password, name, email, employeeId, position);
        this.securityClearance = securityClearance;
        this.adminPrivileges = adminPrivileges;
    }

    public int getSecurityClearance() { return securityClearance; }
    public String getAdminPrivileges() { return adminPrivileges; }

    public Employee createEmployee(String userId, String username, String password,
                                   String name, String email, String employeeId, String position) {

        Employee Employee = new Employee(userId, username, password, name, email, employeeId, position);
        System.out.println("New Employee : " + Employee.getName()+" account has been made.");
        return Employee;
    }

    public Customer createCustomer(String userId, String username, String password,
                                   String name, String email, String address) {

        Customer customer = new Customer(userId, username, password, name, email, address);
        System.out.println("Customer account created, A warm welcome to " + customer.getName());
        return customer;
    }


    public void overrideTransactionLimits(Account account, int newLimit) {

        if (account.getClass() != SavingsAccount.class) {
            System.out.println("Checking Accounts are already unlimited.");
            return;
        }
        SavingsAccount savingsAccount = (SavingsAccount) account;
        savingsAccount.setWithdrawalLimit(newLimit);
        System.out.println(" Transaction limit overridden, New limit is " + newLimit);
    }

    public void resetUserPassword(User user, String newPassword) {
        user.setPassword(newPassword);
        System.out.println("Success: Password reset for user: " + user.getName());
    }

    public void modifyAccountStatus(Account account, String newStatus) {
        account.setStatus(newStatus);
        System.out.println(account.getAccountNumber() + " 's status changed to " + account.getStatus());
    }

    public void modifySystemSettings(double newInterestRate, int newWithdrawalLimit) {
        System.out.println("System Settings Update :");
        System.out.println("New Global Interest Rate: " + (newInterestRate * 100) + "%");
        System.out.println("New Global Default Withdrawal Limit: " + newWithdrawalLimit);
    }
}