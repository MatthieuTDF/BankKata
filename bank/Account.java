package bank;

class Account {

    // Attributes
    private String name;
    private int balance;
    private int overdraft;
    private boolean locked;
    // TODO

    // Constructor
    public Account(String name, int balance, int overdraft, boolean locked){
        this.name = name;
        this.balance = balance;
        this.overdraft = overdraft;
        this.locked = locked;
    }
    // TODO

    // Methods


    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getOverdraft() {
        return overdraft;
    }

    public void setOverdraft(int overdraft) {
        this.overdraft = overdraft;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String toString() {
        // TODO
        return this.name + " | " + this.balance + " | " + this.overdraft + " | " + this.locked + "\n";
    }
}
