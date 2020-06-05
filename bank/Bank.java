package bank;


import java.sql.*;
import java.util.Arrays;

public class Bank {

    /*
        Strings de connection à la base postgres
     */
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    /*
        Strings de connection à la base mysql, à décommenter et compléter avec votre nom de bdd et de user
     */
    // private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    // private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    // private static final String DB_USER = "bank_user";

    private static final String TABLE_NAME = "account";

    private Connection c;

    public Bank() {
        initDb();

        try (Statement s = c.createStatement()) {
            s.executeUpdate("INSERT INTO account(name,balance,threshold) VALUES ('TEST',420,-666)"); //Données de bases
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void initDb() {
        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");

            try (Statement s = c.createStatement()) {
                s.executeUpdate( // Créer une table si elle n'existe pas deja
                        "CREATE TABLE IF NOT EXISTS `account` ( `name` char(255) DEFAULT NULL, `balance` int(11) DEFAULT NULL, `threshold` int(11) DEFAULT NULL, `blocked` varchar(1) NOT NULL DEFAULT 'f' )");
            } catch (Exception e) {
                System.out.println(e.toString());
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public void closeDb() {
        try {
            c.close();
        } catch (SQLException e) {
            System.out.println("Could not close the database : " + e);
        }
    }

    void dropAllTables() {
        try (Statement s = c.createStatement()) {
            s.executeUpdate("DROP TABLE account"); // Supprime la table account
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    public void createNewAccount(String name, int balance, int threshold) {
        if(threshold <=0 && balance >= 0) { // On verifie que la balance est > 0 et que le decouvert est inferieur ou egal a 0
            try (Statement s = c.createStatement()) { // Ajoute un compte a la base de donnée
                s.executeUpdate("INSERT INTO account(name,balance,threshold) VALUES ('" + name + "'," + balance + "," + threshold + ")");
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    public String printAllAccounts() {
        String totalRows = "";
        try (Statement s = c.createStatement()) {
            String query = "SELECT * FROM " + TABLE_NAME;
            ResultSet result =  s.executeQuery(query);
            while(result.next()){ // on parcourt chaque ligne et on colle les resultats de la requete avec des |
                totalRows += result.getString(1)+" | "+result.getString(2)+" | "+result.getString(3)+" | "+ (result.getString(4).equals("f")?"false":"true");
                totalRows += "\n";
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        System.out.print(totalRows);
        return totalRows;
    }

    public void changeBalanceByName(String name, int balanceModifier) {
        try (Statement s = c.createStatement()) { // On recupere toute la table
            String query = "SELECT name,balance,threshold,blocked FROM " + TABLE_NAME;
            ResultSet result =  s.executeQuery(query);
            while(result.next()){
                if(result.getString(1).equals(name)){ // On parcourt les lignes jusqu'au bon nom et si le ocmpte est pas bloqué on modifie la balance
                    if(Integer.parseInt(result.getString(2))+balanceModifier >= Integer.parseInt(result.getString(3)) && result.getString(4).equals("f")) {
                        balanceModifier = Integer.parseInt(result.getString(2)) + balanceModifier;
                        try (Statement w = c.createStatement()) {
                            w.executeUpdate("UPDATE account SET balance=" + balanceModifier + " WHERE name='" + name + "'");
                        } catch (Exception e) {
                            System.out.println(e.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    public void blockAccount(String name) {
        try (Statement s = c.createStatement()) { // On change le f qui est par default et t si le compte est bloqué
            s.executeUpdate("UPDATE account SET blocked='t' WHERE name='"+name+"'");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    // For testing purpose
    String getTableDump() {
        String query = "select * from " + TABLE_NAME;
        String res = "";

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
                res += Arrays.toString(currentRow);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res;
    }
}
