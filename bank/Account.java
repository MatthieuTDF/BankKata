package bank;

class Account {

    private final String name;
    private int balance;
    private int threshold;
    private boolean locked;

    public Account(String name, int balance, int threshold, boolean locked) {
        this.name = name;
        this.balance = balance;
        this.threshold = threshold;
        this.locked = locked;
    }


    // Methods
    //Getters and Setters

    public String getName() { return name; }

    public int getBalance() { return balance; }

    public void setBalance(int balance) { this.balance = balance; }

    public int getThreshold() { return threshold; }

    public void setThreshold(int threshold) { this.threshold = threshold; }

    public boolean isLocked() { return locked; }

    public void setLocked(boolean locked) { this.locked = locked; }


    public String toString() {
        return this.name + " | " + this.balance + " | " + this.threshold + " | " + this.locked + "\n";
    }
}