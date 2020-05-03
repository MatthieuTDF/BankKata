package bank;



class Account {

    // Attributes
    private String name;
    private int balance;
    private int threshold;
    private boolean state;



    // Constructor

    public Account(String name, int balance, int threshold, boolean state) {
        this.name = name;
        this.balance = balance;
        this.threshold = threshold;
        this.state = state;
    }

    // Methods
    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public int getThreshold() {
        return threshold;
    }

    public boolean isState() {
        return state;
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

    public void setState(boolean state) {
        this.state = state;
    }

    public String toString() {
        String result =  this.name + " | " + this.balance + " | " + this.threshold + " | " + this.state + "\n";
        return result;
    }
}
