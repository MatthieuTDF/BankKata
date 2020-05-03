package bank;

class Account {

    // Attributes
    // TODO
    private String name;
    private int balance;
    private int threshold;
    private boolean is_blocked;

    // Constructor
    // TODO
    public Account(String name, int balance, int threshold, boolean is_blocked){
        this.name = name;
        this.balance = balance;
        this.threshold = threshold;
        this.is_blocked = is_blocked;
    }

    // Methods
    // TODO
    public String get_Name() {
        return name;
    }

    public void set_Name(String name) {
        this.name = name;
    }

    public int get_Balance() {
        return balance;
    }

    public void set_Balance(int balance) {
        this.balance = balance;
    }

    public int get_threshold() {
        return threshold;
    }

    public void set_threshold(int threshold) {
        this.threshold = threshold;
    }

    public boolean get_is_blocked() {
        return is_blocked;
    }

    public void set_Is_blocked(boolean is_blocked) {
        this.is_blocked = is_blocked;
    }

    public String toString() {
        // TODO
        String res = "";
        res += "Account name : " + this.name + "\n";
        res += "Account balance : " + this.balance + "\n";
        res += "Account max bank overdraft : " + this.threshold + "\n";

        if (this.is_blocked)res += "Account is blocked \n";



        return res;
    }
}
