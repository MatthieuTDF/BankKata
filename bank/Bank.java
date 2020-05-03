package bank;


import java.sql.*;
import java.util.Arrays;

public class Bank {


    /*
        Strings de connection à la base mysql, à décommenter et compléter avec votre nom de bdd et de user
     */
     private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
     private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
     private static final String DB_USER = "root";

    private static final String DB_PASS = "";

    private static final String TABLE_NAME = "Account";

    private Connection c;

    public Bank() {
        initDb();

        // TODO
    }

    private void initDb() {
        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");

            // TODO Init DB

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
            s.executeUpdate(
                       "DROP SCHEMA public CASCADE;" +
                            "CREATE SCHEMA public;" +
                            "GRANT ALL ON SCHEMA public TO postgres;" +
                            "GRANT ALL ON SCHEMA public TO public;");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    public void createNewAccount(String name, int balance, int threshold) {
        // TODO
                try (Statement s = c.createStatement()) {
                    s.executeUpdate("INSERT INTO " + TABLE_NAME + " " +
                            "(name, balance, threshold, is_blocked) " +
                            "VALUES " +
                            "('" + name + "','" + balance + "','" + threshold + "',false)");
                    System.out.println("compte crée avec succès.");
                } catch (Exception e) {
                    System.out.println(e.toString());
                }

    }


    public String printAllAccounts() {
        // TODO
        String query = "SELECT name, balance, threshold, is_blocked " +
                "FROM " + TABLE_NAME;

        String accountsDisplay= "";

        try (PreparedStatement statement = c.prepareStatement(query)) {
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                accountsDisplay += (new Account(
                        result.getString(1), result.getInt(2),
                        result.getInt(3), result.getBoolean(4)
                )).toString();

                accountsDisplay += "\n";

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return accountsDisplay;
    }

    public void changeBalanceByName(String name, int balanceModifier) {
        // TODO
        String query = "SELECT name, balance, threshold, is_blocked " +
                "FROM " + TABLE_NAME + " WHERE name = " +"'" +name+"'";

        try (PreparedStatement statement = c.prepareStatement(query)) {
            ResultSet result = statement.executeQuery();

            if(!result.next()) {

                System.out.println("Nous n'avons pas trouvé de compte associé à ce nom, veuillez réessayer\n");

            }else{

                if (result.getInt(3) > balanceModifier)System.out.println("Le plafond ne peut pas être supérieur au solde, l'opération n'a pas été effectuée.\n");

                else if (result.getBoolean(4)== true){
                    System.out.println("Le compte que vous tenter de modifier est bloqué, l'opération n'a pas été effectuée.\n");
                }

                else{

                    try (Statement s = c.createStatement()) {
                        s.executeUpdate("UPDATE " + TABLE_NAME +
                                " SET balance = " +
                                balanceModifier +
                                " WHERE name = " + "'"+name+"'" );
                        System.out.println("L'opération a été effectuée avec succès. \n");

                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }

                }


            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

    public void blockAccount(String name) {
        // TODO
        String query = "SELECT is_blocked " +
                "FROM " + TABLE_NAME + " WHERE name = " +"'" +name+"'";

        try (PreparedStatement statement = c.prepareStatement(query)) {
            ResultSet result = statement.executeQuery();

            if(!result.next())System.out.println("Désolé nous n'avons pas trouvé de compte associé à ce nom, veuillez réessayer");
            else{
                try (Statement s = c.createStatement()) {
                    s.executeUpdate("UPDATE " + TABLE_NAME + " SET " +
                            " is_blocked = true WHERE name = '"+name+"'");
                    System.out.println("L'opération à été effectuée avec succès");
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    // For testing purpose
    public String getTableDump() {
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
