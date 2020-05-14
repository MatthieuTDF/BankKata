package bank;

class Account {

    String name;
    int balance, threshold;
    boolean block;

    public Account(String name, int balance, int threshold, boolean block) {
        this.name = name;
        this.balance = balance;
        this.threshold = threshold;
        this.block = block;
    }

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

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    @Override
    public String toString() {
        return this.name + " | " +
                this.balance + " | " +
                this.threshold + " | " +
                this.block + "\n";
    }
}