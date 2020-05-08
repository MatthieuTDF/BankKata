package bank;

class Account {

    // Attributes
    // TODO
    private String fullName;
    private Integer amount;
    private Integer roof;
    private Boolean isBlocked;

    // Constructor
    // TODO
    public Account(String fullName, Integer amount, Integer roof, Boolean isBlocked){
        this.fullName = fullName;
        this.amount = amount;
        this.roof = roof;
        this.isBlocked = isBlocked;
    }

    // Methods
    // TODO

    //Les getteurs et setter je les ai importé depuis l'option generate
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getRoof() {
        return roof;
    }

    public void setRoof(Integer roof) {
        this.roof = roof;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }
    //Getter setter fin
    public void wireAmount(Integer wire){
        int total = amount + wire;
        if (total <= roof){
            this.amount = total;
        }
    }



    public String toString() {
        // TODO
        return "Nom complet: " + this.fullName + "; Solde du compte: " + this.amount + "; Plafond de virements: " + this.roof + "; Compte bloqué: " + this.isBlocked;
    }
}
