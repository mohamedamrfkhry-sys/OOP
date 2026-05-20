public class SavingsAccount extends Account {
    private double interestRate;
    private double minimumBalance;
    private double withdrawalLimit;
    private int withdrawalsThisMonth;

    public SavingsAccount(String accountNumber, Customer owner) {
        super(accountNumber, owner);
        interestRate = 0.02;
        minimumBalance = 100;
        this.withdrawalLimit = 3;
        this.withdrawalsThisMonth = 0;
    }


    public void setWithdrawalLimit(int limit) {
        if (limit > 0) {
            this.withdrawalLimit = limit;
        }
    }

    public double getInterestRate()    { return interestRate; }
    public double getMinimumBalance()  { return minimumBalance; }
    public double getWithdrawalLimit() { return withdrawalLimit; }
    public int getWithdrawalsThisMonth() { return withdrawalsThisMonth; }

    public void setInterestRate(double rate)      { if (rate > 0) this.interestRate = rate; }
    public void setMinimumBalance(double minimum) { if (minimum >= 0) this.minimumBalance = minimum; }


    @Override
    public boolean withdraw(double amount, String transactionId) {
        if (withdrawalsThisMonth >= withdrawalLimit) {
            System.out.println("Monthly withdrawing limit reached, process cannot be continued.");
            return false;
        } else if ((getBalance() - amount) < minimumBalance) {
            System.out.println("Insufficient funds — balance cannot drop below the minimum of $" + minimumBalance);
            return false;
        } else {
            setBalance(getBalance() - amount);
            withdrawalsThisMonth++;
            System.out.println(amount + " was withdrawn successfully.");
            Transaction t = new Transaction(transactionId, amount, "Withdrawal", this, null, "COMPLETED");
            getTransactions().add(t);
            return true;
        }
    }


    public double calcInterest() {
        return getBalance() * (interestRate / 12);
    }

    public void MonthlyInterest() {
        withdrawalsThisMonth = 0;
        double interest = calcInterest();
        setBalance(getBalance() + interest);

        System.out.println("Monthly interest applied is " + interest);
    }
}

