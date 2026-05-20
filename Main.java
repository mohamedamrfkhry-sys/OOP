import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class Main extends JFrame {

    // ── Core ──────────────────────────────────────────────────────────────────
    private Bank bank;
    private User currentUser;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // ── Login ─────────────────────────────────────────────────────────────────
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    // ── Customer Dashboard ────────────────────────────────────────────────────
    private JLabel welcomeLabel;
    private DefaultTableModel accountTableModel;
    private DefaultTableModel txTableModel;
    private JTextField accNumField;
    private JTextField amountField;
    private JTextField toAccField;

    // ── Employee Dashboard ────────────────────────────────────────────────────
    private JLabel empWelcomeLabel;
    private DefaultTableModel empCustomerTableModel;
    private DefaultTableModel empAccountTableModel;
    private JTextField empAccNumField;
    private JTextField empAmountField;
    private JTextField empToAccField;

    // ── Admin Dashboard ───────────────────────────────────────────────────────
    private JLabel adminWelcomeLabel;
    private DefaultTableModel adminUserTableModel;
    private DefaultTableModel adminAccountTableModel;

    // ─────────────────────────────────────────────────────────────────────────
    public Main() {
        bank = new Bank();
        loadDummyData();
        setTitle("Alamein Banking System");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(buildLoginPanel(),      "LOGIN");
        mainPanel.add(buildCustomerDashboard(), "DASHBOARD");
        mainPanel.add(buildEmployeeDashboard(), "EMPLOYEE_DASHBOARD");
        mainPanel.add(buildAdminDashboard(),    "ADMIN_DASHBOARD");
        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  LOGIN
    // ═════════════════════════════════════════════════════════════════════════

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Login"));
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        form.add(loginBtn);
        form.add(errorLabel);
        panel.add(form);
        return panel;
    }

    private void doLogin() {
        User found = bank.findUser(usernameField.getText().trim());
        if (found != null && found.login(new String(passwordField.getPassword()))) {
            currentUser = found;
            usernameField.setText("");
            passwordField.setText("");
            errorLabel.setText(" ");
            // Admin must be checked before Employee (Admin extends Employee)
            if (currentUser instanceof Admin) {
                refreshAdminDashboard();
                cardLayout.show(mainPanel, "ADMIN_DASHBOARD");
            } else if (currentUser instanceof Employee) {
                refreshEmployeeDashboard();
                cardLayout.show(mainPanel, "EMPLOYEE_DASHBOARD");
            } else if (currentUser instanceof Customer) {
                refreshCustomerDashboard();
                cardLayout.show(mainPanel, "DASHBOARD");
            }
        } else {
            errorLabel.setText("Invalid username or password.");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CUSTOMER DASHBOARD
    // ═════════════════════════════════════════════════════════════════════════

    private JPanel buildCustomerDashboard() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        welcomeLabel = new JLabel("Welcome");
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> doLogout());
        JPanel top = new JPanel(new BorderLayout());
        top.add(welcomeLabel, BorderLayout.WEST);
        top.add(logoutBtn, BorderLayout.EAST);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Accounts",       buildAccountsTab());
        tabs.addTab("Transactions",   buildTransactionsTab());
        tabs.addTab("Actions",        buildActionsTab());
        tabs.addTab("Create Account", buildCreateAccountTab());
        panel.add(top,  BorderLayout.NORTH);
        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAccountsTab() {
        accountTableModel = new DefaultTableModel(
                new String[]{"Account #", "Type", "Balance", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(new JTable(accountTableModel)));
        return p;
    }

    private JPanel buildTransactionsTab() {
        txTableModel = new DefaultTableModel(
                new String[]{"TX ID", "Account", "Type", "Amount", "Date"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(new JTable(txTableModel)));
        return p;
    }

    private JPanel buildActionsTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        accNumField = new JTextField();
        amountField = new JTextField();
        toAccField  = new JTextField();
        JButton depositBtn  = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton transferBtn = new JButton("Transfer");
        depositBtn .addActionListener(e -> doTransaction("Deposit",    null));
        withdrawBtn.addActionListener(e -> doTransaction("Withdrawal", null));
        transferBtn.addActionListener(e -> doTransaction("Transfer",   toAccField.getText().trim()));
        form.add(new JLabel("From Account :")); form.add(accNumField);
        form.add(new JLabel("Amount :"));       form.add(amountField);
        form.add(depositBtn);                   form.add(withdrawBtn);
        form.add(new JLabel("To Account #:"));  form.add(toAccField);
        form.add(transferBtn);                  form.add(new JLabel(""));
        panel.add(form);
        return panel;
    }

    /** Lets the logged-in customer open a new Checking or Savings account. */
    private JPanel buildCreateAccountTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Open New Account"));
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Checking", "Savings"});
        JButton createBtn = new JButton("Create Account");
        createBtn.addActionListener(e -> {
            if (!(currentUser instanceof Customer)) return;
            Customer cust = (Customer) currentUser;
            String accNum = "ACC" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            String selected = (String) typeBox.getSelectedItem();
            Account newAcc = "Savings".equals(selected)
                    ? new SavingsAccount(accNum, cust)
                    : new CheckingAccount(accNum, cust);
            bank.createAccount(newAcc, cust);
            bank.saveData();
            JOptionPane.showMessageDialog(this,
                    selected + " account created successfully!\nAccount #: " + accNum);
            refreshCustomerDashboard();
        });
        form.add(new JLabel("Account Type:")); form.add(typeBox);
        form.add(createBtn);                   form.add(new JLabel(""));
        panel.add(form);
        return panel;
    }

    private void doTransaction(String type, String toAcc) {
        try {
            String accNum = accNumField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());
            String txId = "TXN_" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            boolean ok = bank.processTransaction(accNum, type, amount, toAcc, txId);
            if (ok) {
                JOptionPane.showMessageDialog(this, type + " successful!");
                amountField.setText("");
                refreshCustomerDashboard();
            } else {
                JOptionPane.showMessageDialog(this, type + " failed.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid amount.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshCustomerDashboard() {
        Customer cust = (Customer) currentUser;
        welcomeLabel.setText("Welcome, " + cust.getName());
        accountTableModel.setRowCount(0);
        txTableModel.setRowCount(0);
        List<Account> accounts = cust.getAccounts();
        if (accounts == null) return;
        for (Account acc : accounts) {
            accountTableModel.addRow(new Object[]{
                    acc.getAccountNumber(),
                    acc instanceof SavingsAccount ? "Savings" : "Checking",
                    String.format("$%.2f", acc.getBalance()),
                    acc.getStatus()
            });
            List<Transaction> txList = acc.getTransactions();
            if (txList == null) continue;
            for (int i = txList.size() - 1; i >= Math.max(0, txList.size() - 20); i--) {
                Transaction tx = txList.get(i);
                txTableModel.addRow(new Object[]{
                        tx.getTransactionId(),
                        acc.getAccountNumber(),
                        tx.getType(),
                        String.format("$%.2f", tx.getAmount()),
                        tx.getTimestamp()
                });
            }
        }
    }

    /** Kept for backward compatibility — delegates to refreshCustomerDashboard(). */
    private void refreshDashboard() {
        refreshCustomerDashboard();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  EMPLOYEE DASHBOARD
    // ═════════════════════════════════════════════════════════════════════════

    private JPanel buildEmployeeDashboard() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        empWelcomeLabel = new JLabel("Welcome");
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> doLogout());
        JPanel top = new JPanel(new BorderLayout());
        top.add(empWelcomeLabel, BorderLayout.WEST);
        top.add(logoutBtn,       BorderLayout.EAST);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Customers",           buildEmpCustomersTab());
        tabs.addTab("All Accounts",        buildEmpAccountsTab());
        tabs.addTab("Process Transaction", buildEmpTransactionTab());
        panel.add(top,  BorderLayout.NORTH);
        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildEmpCustomersTab() {
        empCustomerTableModel = new DefaultTableModel(
                new String[]{"User ID", "Username", "Name", "Email"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JPanel p = new JPanel(new BorderLayout());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshEmployeeDashboard());
        p.add(new JScrollPane(new JTable(empCustomerTableModel)), BorderLayout.CENTER);
        p.add(refreshBtn, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildEmpAccountsTab() {
        empAccountTableModel = new DefaultTableModel(
                new String[]{"Account #", "Type", "Balance", "Status", "Owner"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JPanel p = new JPanel(new BorderLayout());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshEmployeeDashboard());
        p.add(new JScrollPane(new JTable(empAccountTableModel)), BorderLayout.CENTER);
        p.add(refreshBtn, BorderLayout.SOUTH);
        return p;
    }

    /** Employees can process deposits, withdrawals, and transfers for any account. */
    private JPanel buildEmpTransactionTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        empAccNumField = new JTextField();
        empAmountField = new JTextField();
        empToAccField  = new JTextField();
        JButton depositBtn  = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton transferBtn = new JButton("Transfer");
        depositBtn .addActionListener(e -> doEmpTransaction("Deposit",    null));
        withdrawBtn.addActionListener(e -> doEmpTransaction("Withdrawal", null));
        transferBtn.addActionListener(e -> doEmpTransaction("Transfer",   empToAccField.getText().trim()));
        form.add(new JLabel("Account #:"));    form.add(empAccNumField);
        form.add(new JLabel("Amount :"));      form.add(empAmountField);
        form.add(depositBtn);                  form.add(withdrawBtn);
        form.add(new JLabel("To Account #:")); form.add(empToAccField);
        form.add(transferBtn);                 form.add(new JLabel(""));
        panel.add(form);
        return panel;
    }

    private void doEmpTransaction(String type, String toAcc) {
        try {
            String accNum = empAccNumField.getText().trim();
            double amount = Double.parseDouble(empAmountField.getText().trim());
            String txId = "TXN_" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            boolean ok = bank.processTransaction(accNum, type, amount, toAcc, txId);
            if (ok) {
                JOptionPane.showMessageDialog(this, type + " successful!");
                empAmountField.setText("");
                refreshEmployeeDashboard();
            } else {
                JOptionPane.showMessageDialog(this, type + " failed.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid amount.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshEmployeeDashboard() {
        Employee emp = (Employee) currentUser;
        empWelcomeLabel.setText("Welcome, " + emp.getName() + " [" + emp.getPosition() + "]");
        empCustomerTableModel.setRowCount(0);
        empAccountTableModel.setRowCount(0);
        for (User u : bank.getUsers()) {
            if (u instanceof Customer) {
                empCustomerTableModel.addRow(new Object[]{
                        u.getUserId(), u.getUserName(), u.getName(), u.getEmail()
                });
            }
        }
        for (Account acc : bank.getAccounts()) {
            empAccountTableModel.addRow(new Object[]{
                    acc.getAccountNumber(),
                    acc instanceof SavingsAccount ? "Savings" : "Checking",
                    String.format("$%.2f", acc.getBalance()),
                    acc.getStatus(),
                    acc.getOwner().getName()
            });
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ADMIN DASHBOARD
    // ═════════════════════════════════════════════════════════════════════════

    private JPanel buildAdminDashboard() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        adminWelcomeLabel = new JLabel("Welcome");
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> doLogout());
        JPanel top = new JPanel(new BorderLayout());
        top.add(adminWelcomeLabel, BorderLayout.WEST);
        top.add(logoutBtn,         BorderLayout.EAST);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Users",               buildAdminUsersTab());
        tabs.addTab("Accounts",            buildAdminAccountsTab());
        tabs.addTab("Create User",         buildAdminCreateUserTab());
        tabs.addTab("Account Management",  buildAdminAccountMgmtTab());
        tabs.addTab("System Settings",     buildAdminSystemTab());
        panel.add(top,  BorderLayout.NORTH);
        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    /** Displays all users. Includes a Reset Password button that prompts via dialogs. */
    private JPanel buildAdminUsersTab() {
        adminUserTableModel = new DefaultTableModel(
                new String[]{"User ID", "Username", "Name", "Email", "Role"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JButton refreshBtn  = new JButton("Refresh");
        JButton resetPwdBtn = new JButton("Reset Password");
        refreshBtn .addActionListener(e -> refreshAdminDashboard());
        resetPwdBtn.addActionListener(e -> doAdminResetPassword());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.add(refreshBtn);
        south.add(resetPwdBtn);
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(new JTable(adminUserTableModel)), BorderLayout.CENTER);
        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    /** Displays all bank accounts. */
    private JPanel buildAdminAccountsTab() {
        adminAccountTableModel = new DefaultTableModel(
                new String[]{"Account #", "Type", "Balance", "Status", "Owner"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JPanel p = new JPanel(new BorderLayout());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshAdminDashboard());
        p.add(new JScrollPane(new JTable(adminAccountTableModel)), BorderLayout.CENTER);
        p.add(refreshBtn, BorderLayout.SOUTH);
        return p;
    }

    /**
     * Allows an admin to create a Customer or Employee.
     * A single "extra" field is used for Address (Customer) or Position (Employee);
     * its label updates dynamically when the role dropdown changes.
     */
    private JPanel buildAdminCreateUserTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Create New User"));
        JComboBox<String> roleBox      = new JComboBox<>(new String[]{"Customer", "Employee"});
        JTextField  newUsernameField   = new JTextField();
        JPasswordField newPasswordField = new JPasswordField();
        JTextField  newNameField       = new JTextField();
        JTextField  newEmailField      = new JTextField();
        JLabel      extraLabel         = new JLabel("Address:");
        JTextField  newExtraField      = new JTextField();
        // Dynamically relabel the last field based on selected role
        roleBox.addActionListener(e ->
                extraLabel.setText("Customer".equals(roleBox.getSelectedItem())
                        ? "Address:" : "Position:"));
        JButton createBtn = new JButton("Create");
        createBtn.addActionListener(e -> doAdminCreateUser(
                roleBox, newUsernameField, newPasswordField,
                newNameField, newEmailField, newExtraField));
        form.add(new JLabel("Role:"));     form.add(roleBox);
        form.add(new JLabel("Username:")); form.add(newUsernameField);
        form.add(new JLabel("Password:")); form.add(newPasswordField);
        form.add(new JLabel("Name:"));     form.add(newNameField);
        form.add(new JLabel("Email:"));    form.add(newEmailField);
        form.add(extraLabel);              form.add(newExtraField);
        form.add(createBtn);               form.add(new JLabel(""));
        panel.add(form);
        return panel;
    }

    /** Modify account status or override savings withdrawal limit for any account. */
    private JPanel buildAdminAccountMgmtTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField   acctField  = new JTextField();
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active", "Inactive", "Frozen"});
        JTextField   limitField = new JTextField();
        JButton changeStatusBtn = new JButton("Change Status");
        JButton overrideLimitBtn = new JButton("Override Withdrawal Limit");
        changeStatusBtn.addActionListener(e -> {
            if (!(currentUser instanceof Admin)) return;
            Admin admin = (Admin) currentUser;
            Account acc = bank.findAccount(acctField.getText().trim());
            if (acc == null) {
                JOptionPane.showMessageDialog(this, "Account not found.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            admin.modifyAccountStatus(acc, (String) statusBox.getSelectedItem());
            bank.saveData();
            JOptionPane.showMessageDialog(this,
                    "Account status updated to: " + acc.getStatus());
            refreshAdminDashboard();
        });
        overrideLimitBtn.addActionListener(e -> {
            if (!(currentUser instanceof Admin)) return;
            Admin admin = (Admin) currentUser;
            Account acc = bank.findAccount(acctField.getText().trim());
            if (acc == null) {
                JOptionPane.showMessageDialog(this, "Account not found.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int limit = Integer.parseInt(limitField.getText().trim());
                admin.overrideTransactionLimits(acc, limit);
                JOptionPane.showMessageDialog(this, "Withdrawal limit updated.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid integer limit.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        form.add(new JLabel("Account #:"));                   form.add(acctField);
        form.add(new JLabel("New Status:"));                  form.add(statusBox);
        form.add(changeStatusBtn);                            form.add(new JLabel(""));
        form.add(new JLabel("Withdrawal Limit (Savings):")); form.add(limitField);
        form.add(overrideLimitBtn);                           form.add(new JLabel(""));
        panel.add(form);
        return panel;
    }

    /** Calls Admin.modifySystemSettings() with the entered interest rate and withdrawal limit. */
    private JPanel buildAdminSystemTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("System Settings"));
        JTextField interestRateField = new JTextField();
        JTextField globalLimitField  = new JTextField();
        JButton applyBtn = new JButton("Apply Settings");
        applyBtn.addActionListener(e -> {
            if (!(currentUser instanceof Admin)) return;
            Admin admin = (Admin) currentUser;
            try {
                double rate  = Double.parseDouble(interestRateField.getText().trim());
                int    limit = Integer.parseInt(globalLimitField.getText().trim());
                admin.modifySystemSettings(rate, limit);
                JOptionPane.showMessageDialog(this,
                        "System settings updated.\n"
                                + "Interest Rate: " + (rate * 100) + "%\n"
                                + "Global Withdrawal Limit: " + limit);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter valid numeric values.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        form.add(new JLabel("Interest Rate (e.g. 0.03 = 3%):")); form.add(interestRateField);
        form.add(new JLabel("Global Withdrawal Limit:"));          form.add(globalLimitField);
        form.add(applyBtn);                                        form.add(new JLabel(""));
        panel.add(form);
        return panel;
    }

    private void doAdminCreateUser(JComboBox<String> roleBox, JTextField usernameF,
                                   JPasswordField passwordF, JTextField nameF,
                                   JTextField emailF, JTextField extraF) {
        if (!(currentUser instanceof Admin)) return;
        Admin  admin    = (Admin) currentUser;
        String role     = (String) roleBox.getSelectedItem();
        String uname    = usernameF.getText().trim();
        String pwd      = new String(passwordF.getPassword()).trim();
        String fullName = nameF.getText().trim();
        String email    = emailF.getText().trim();
        String extra    = extraF.getText().trim();
        if (uname.isEmpty() || pwd.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String userId = "U" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        if ("Customer".equals(role)) {
            Customer newCust = admin.createCustomer(userId, uname, pwd, fullName, email, extra);
            bank.createUser(newCust);
            bank.saveData();
            JOptionPane.showMessageDialog(this, "Customer '" + fullName + "' created successfully.");
        } else {
            String empId = "EMP" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            Employee newEmp = admin.createEmployee(
                    userId, uname, pwd, fullName, email, empId, extra);
            bank.createUser(newEmp);
            bank.saveData();
            JOptionPane.showMessageDialog(this, "Employee '" + fullName + "' created successfully.");
        }
        usernameF.setText(""); passwordF.setText(""); nameF.setText("");
        emailF.setText("");    extraF.setText("");
        refreshAdminDashboard();
    }

    /** Prompts for a username then a new password, then calls Admin.resetUserPassword(). */
    private void doAdminResetPassword() {
        if (!(currentUser instanceof Admin)) return;
        Admin admin = (Admin) currentUser;
        String uname = JOptionPane.showInputDialog(this, "Enter username to reset password:");
        if (uname == null || uname.trim().isEmpty()) return;
        User target = bank.findUser(uname.trim());
        if (target == null) {
            JOptionPane.showMessageDialog(this, "User not found.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String newPwd = JOptionPane.showInputDialog(this,
                "Enter new password for " + target.getName() + ":");
        if (newPwd == null || newPwd.trim().isEmpty()) return;
        admin.resetUserPassword(target, newPwd.trim());
        bank.saveData();
        JOptionPane.showMessageDialog(this, "Password reset for: " + target.getName());
    }

    private void refreshAdminDashboard() {
        Admin admin = (Admin) currentUser;
        adminWelcomeLabel.setText("Welcome, " + admin.getName() + " [Admin]");
        adminUserTableModel.setRowCount(0);
        adminAccountTableModel.setRowCount(0);
        for (User u : bank.getUsers()) {
            String role = u instanceof Admin    ? "Admin"
                    : u instanceof Employee ? "Employee"
                    :                        "Customer";
            adminUserTableModel.addRow(new Object[]{
                    u.getUserId(), u.getUserName(), u.getName(), u.getEmail(), role
            });
        }
        for (Account acc : bank.getAccounts()) {
            adminAccountTableModel.addRow(new Object[]{
                    acc.getAccountNumber(),
                    acc instanceof SavingsAccount ? "Savings" : "Checking",
                    String.format("$%.2f", acc.getBalance()),
                    acc.getStatus(),
                    acc.getOwner().getName()
            });
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  SHARED HELPERS
    // ═════════════════════════════════════════════════════════════════════════

    private void doLogout() {
        if (currentUser != null) {
            currentUser.logout();
            currentUser = null;
        }
        cardLayout.show(mainPanel, "LOGIN");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  DUMMY DATA
    // ═════════════════════════════════════════════════════════════════════════

    private void loadDummyData() {
        // Only load dummy data if the bank has no users yet (first run)
        if (!bank.getUsers().isEmpty()) return;

        // Customer
        Customer john = new Customer("U001", "john123", "password",
                "John Doe", "john@email.com", "123 Main St");
        CheckingAccount checking = new CheckingAccount("10001", john);
        SavingsAccount  savings  = new SavingsAccount("10002", john);
        bank.createUser(john);
        bank.createAccount(checking, john);
        bank.createAccount(savings, john);
        bank.processTransaction("10001", "Deposit", 1000.0, null, "INIT_01");
        bank.processTransaction("10002", "Deposit",  500.0, null, "INIT_02");

        // Employee
        Employee emp = new Employee("U002", "emp001", "emp123",
                "Jane Smith", "jane@bank.com", "EMP001", "Teller");
        bank.createUser(emp);

        // Admin
        Admin admin = new Admin("U003", "admin001", "admin123",
                "Robert Admin", "admin@bank.com", "EMP000", "Administrator",
                5, "FULL_ACCESS");
        bank.createUser(admin);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ENTRY POINT
    // ═════════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}