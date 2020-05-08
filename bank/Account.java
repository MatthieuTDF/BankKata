package bank;

class Account {

    // Attributes
    private String name;
    private int argent;
    private int decouvert;
    private boolean test;

    // Constructor

    public Account(String name, int argent, int decouvert, boolean test){
        this.name = name;
        this.argent = argent;
        this.decouvert = decouvert;
        this.test = test;
    }

    // Methods

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getargent() {
        return argent;
    }

    public void setargent(int argent) {
        this.argent = argent;
    }

    public int getDecouvert() {
        return decouvert;
    }

    public void setDecouvert(int decouvert) {
        this.decouvert = decouvert;
    }

    public boolean istest() {
        return test;
    }

    public void settest(boolean test) {
        this.test = test;
    }

    public String toString() {
        return  this.name + " | " + this.argent + " | " + this.decouvert + " | " + this.test + "\n";
    }
}