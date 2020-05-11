package bank;

class Account {

    // Attributes
    private String name;
    private int balance;
    private int threshold;
    private int lockedOut;



    public Account(String name, int balance, int threshold, int lockedOut) {
        this.name = name;
        this.balance = balance;
        this.threshold = threshold;
        this.lockedOut = lockedOut;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public int getThreshold() {
        return threshold;
    }

    public int isLockedOut() {
        return lockedOut;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setLockedOut(int lockedOut) {
        this.lockedOut = lockedOut;
    }

    public String toString() {

        int locked = this.lockedOut;
        String status = "";

        if (locked == 0)
            status = "false";
        else
            status = "true";

        return this.name + " | " + this.balance + " | " + this.threshold + " | " + status + "\n";
    }


}
