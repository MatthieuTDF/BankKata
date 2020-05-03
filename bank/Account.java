package bank;

class Account {

    // Attributes
    private String name;
    private int balance;
    private int threshold;
    private boolean blocked;

    // Constructor

    public Account(String name, int balance, int threshold, boolean blocked){
        this.name = name;
        this.balance = balance;
        this.threshold = threshold;
        this.blocked = blocked;
    }

    // Methods

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String toString() {
        return  this.name + " | " + this.balance + " | " + this.threshold + " | " + this.blocked + "\n";
    }
}