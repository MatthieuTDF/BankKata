package bank;

class Account {

    // Attributes
    private final String name;
    private int balance;
    private final int overdraft;
    private boolean banned;

    // Constructor
    public Account(String name, int balance, int overdraft, boolean banned){
        this.name = name;
        this.balance = balance;
        this.overdraft = overdraft;
        this.banned = banned;
    }

    // Methods
    public String getName() {
        return this.name;
    }

    public int getBalance() {
        return this.balance;
    }
    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getOverdraft() {
        return this.overdraft;
    }

    public boolean isBanned() {
        return this.banned;
    }
    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public String toString() {
        return this.name + " | " + this.balance + " | " + this.overdraft + " | " + this.banned + "\n";
    }
}
