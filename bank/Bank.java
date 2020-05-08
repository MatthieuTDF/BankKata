// INFOS : fait avec Pauline


package bank;

//Imports
import java.sql.*;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Bank {

    /*
        creation de constantes pour la connection à la base
    */
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mysql?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private static final String TABLE_NAME = "accounts";

    private Connection c;

    public Bank() {
        initDb();
    }

    // Ouverture de la base
    private void initDb() {
        try {
            Class.forName(JDBC_DRIVER);
            this.c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");

            createTable();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    // Creation de la table "account"
    private void createTable(){

        try (Statement s = this.c.createStatement()) {
            s.executeUpdate("CREATE TABLE " + Bank.TABLE_NAME + "(\n" +
                    "name VARCHAR(255) NOT NULL,\n" +
                    "balance INT NOT NULL,\n" +
                    "threshold INT NOT NULL,\n" +
                    "locked BOOLEAN NOT NULL DEFAULT false,\n" +   // changement de "locked" en false
                    "PRIMARY KEY (name))");
            System.out.println("Table 'accounts' was created successfully"); // message de succes

        } catch (Exception e) {                 // si erreur recuperation du code
            System.out.println(e.toString());   // print la cause de l'erreur
        }
    }

    public void closeDb() { //fermeture de la base
        try {
            c.close();     // fermeture de la connection
        } catch (SQLException e) {
            System.out.println("Could not close the database : " + e); // sinon on recupere le code d'erreur
        }
    }

    void dropAllTables() { // Supprimer une table

        try (Statement s = this.c.createStatement()) { // tentative de supprssion
            s.executeUpdate(
                    "DROP TABLE " + TABLE_NAME); // commande sql associée
            System.out.println("Table dropped");
        } catch (Exception e) {
            System.out.println(e.toString());     // sinon on renvoit l'erreur
        }
    }


    public void createNewAccount(String name, int balance, int threshold) { // CREATION D'UN NOUVAEU COMPTE
        Pattern p = Pattern.compile("([a-zA-Z]*([ ]|-)?)*");
        if (p.matcher(name).matches()) {                                        // Si la syntax match
            Account account = validAccount(name);
            if (account == null) {                                              // Si le compte n'existe pas
                if (threshold <= 0) {                                           // Si le découvert est positif
                    String sql = "INSERT INTO " + TABLE_NAME + " (name, balance, threshold) " + // envoie de la commande sql
                            "VALUES ('" + name + "','" + balance + "','" + threshold + "')";

                    try (Statement s = c.createStatement()) { // tentative de création
                        s.executeUpdate(sql);
                        System.out.println("Account created");// notification de succes
                    } catch (Exception e) {
                        System.out.println(e.toString()); // si fail, renvoit l'erreur
                    }

                } else { System.out.println("Threshold can't be superior to 0"); }    // Gestion des exeptions
            } else { System.out.println("The name : " + name + " is already used"); } // Gestion des exeptions
        } else { System.out.println("The name doesnt respect syntax"); }              // Gestion des exeptions
    }

    public String printAllAccounts() {  // print tous les comptes
        String query = "SELECT * FROM " + TABLE_NAME;  // séléction de tous ls comptes
        StringBuilder accounts = new StringBuilder(); // on créer une string contenant les champs
        try (Statement s = c.createStatement()) {       // tentative de création
            ResultSet r = s.executeQuery(query);
            while (r.next()) {                                                       //Creation de l'objet account
                accounts.append((new Account(r.getString(1),
                        r.getInt(2),
                        r.getInt(3),
                        r.getBoolean(4))).toString());
            }
        } catch (Exception e) {     //si fail, renvoi el'erreur
            System.out.println(e.toString());
        }
        return accounts.toString(); // return la string
    }

    public void changeBalanceByName(String name, int balanceModifier) {             //modifier la valeur du compte
        String query = "UPDATE " + TABLE_NAME + " SET balance = balance + "         // sql pour recupérer le compte
                + balanceModifier + " WHERE name = '" + name + "'";

        try (Statement s = this.c.createStatement()) {
            Account account = validAccount(name);

            if (account != null) {
                if (!account.isLocked()) {                                          // Si le compte n'est pas bloqué
                    if (balanceModifier >= account.getThreshold()) {                //Si la modification ne depasse pas le découvert
                        s.executeUpdate(query);                                     // Mise à jours de la valeur
                    } else { System.out.println("Treshold hit"); }                  // Gestion des exeptions
                } else { System.out.println("Account is locked"); }                 // Gestion des exeptions
            } else { System.out.println("No account with the name : " + name); }    // Gestion des exeptions

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void blockAccount(String name) { // geler un compte
        String sql = "UPDATE " + TABLE_NAME + " SET locked = 1 WHERE name = '" + name + "'"; // récupere le compte par son nom

        try (Statement s = c.createStatement()) {
            if (validAccount(name) != null) { s.executeUpdate(sql); }       // verification de la validité de la requete
            else { System.out.println("No account with the name : " + name); }// sil il n'y a pas de compte à ce nom

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public Account validAccount(String name) { // verifier si le nom n'existe pas déjà
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE name = '" + name + "'"; // recherche dans la base
        try (Statement s = this.c.createStatement()) {
            ResultSet set = s.executeQuery(query);

            if (set.next()) {                                                           // si le compte est existant
                return new Account(set.getString(1), set.getInt(2),
                        set.getInt(3), set.getBoolean(4));
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    // For testing purpose
    String getTableDump() {
        String query = "select * from " + TABLE_NAME;
        StringBuilder res = new StringBuilder();

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();

            // Getting nb colmun from meta data
            int nbColumns = r.getMetaData().getColumnCount();

            // while there is a next row
            while (r.next()){
                String[] currentRow = new String[nbColumns];

                // For each column in the row
                for (int i = 1 ; i <= nbColumns ; i++) {
                    currentRow[i - 1] = r.getString(i);
                }
                res.append(Arrays.toString(currentRow));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res.toString();
    }

}