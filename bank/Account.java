package bank;

class Account {

    // Attributes
    // TODO
    private String $name;
    private double $amount;
    private double $allowed;
    private int $status;

    // Constructor
    // TODO
    public Account(String name, double amount, double allowed){

        name = $name;
        amount = $amount;
        allowed = $allowed;
        $status = 1;

    }

    // Methods
    // TODO

    //Getter
    public double get$allowed() {
        return $allowed;
    }

    public double get$amount() {
        return $amount;
    }

    public int get$status() {
        return $status;
    }

    public String get$name() {
        return $name;
    }

    //Setter
    public void set$allowed(double $allowed) {
        this.$allowed = $allowed;
    }

    public void set$amount(double $amount) {
        this.$amount = $amount;
    }

    public void set$name(String $name) {
        this.$name = $name;
    }

    public void set$status(int $status) {
        this.$status = $status;
    }

    public String toString() {
        // TODO
        String fields = this.get$name() + this.get$amount() + this.get$allowed() + this.get$status();
        return fields;
    }
}
