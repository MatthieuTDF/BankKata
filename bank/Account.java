package bank;

class Account {

    // Attributes
    private String name;
    private int balance;
    private int threshold;
    private int status;

    // Constructor
    public Account(String name, int balance, int threshold){
        this.name = name;
        this.balance = balance;
        this.threshold = threshold;
        this.status = 1;
    }

    // Methods


    public int getBalance() {
        return balance;
    }

    public int getStatus() {
        return status;
    }

    public int getThreshold() {
        return threshold;
    }

    public String getName() {
        return name;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public String toString() {
        String result = this.name + " " + this.balance + " " + this.threshold + " " + this.status;
        return result;
    }
}
