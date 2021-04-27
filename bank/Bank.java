package bank;


import java.sql.*;
import java.util.Arrays;

public class Bank {

    /*
        Strings de connection à la base postgres
     */
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5439/postgres";
    private static final String DB_USER = "postgres";

    /*
        Strings de connection à la base mysql, à décommenter et compléter avec votre nom de bdd et de user
     */
    // private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    // private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    // private static final String DB_USER = "bank_user";

    private static final String DB_PASS = "1234";

    private static final String TABLE_NAME = "accounts";

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

            // TODO Init DB (Création schéma base)

            try (Statement s = c.createStatement()) {
                s.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(\n" +
                                "\tName VARCHAR(30) NOT NULL,\n" +
                                "\tBalance INT DEFAULT NULL,\n" +
                                "\tThreshold INT DEFAULT NULL,\n" +
                                "\tBlocked BOOLEAN DEFAULT FALSE,\n" +
                                "\tPRIMARY KEY (Name)\n" +
                                ");");
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
            s.executeUpdate(
                       "DROP SCHEMA public CASCADE;" +
                            "CREATE SCHEMA public;" +
                            "GRANT ALL ON SCHEMA public TO postgres;" +
                            "GRANT ALL ON SCHEMA public TO public;");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    public void createNewAccount(String name_account, int balance_account, int threshold_account) {
        if ((threshold_account <= 0) && (balance_account >= threshold_account)) {
            try (Statement s = c.createStatement()) {
                s.executeUpdate(
                        "INSERT INTO accounts (name, balance, threshold) VALUES ('" + name_account + "', '" + balance_account + "', '" + threshold_account + "');"
                );
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    public String printAllAccounts() {
        String query = "select name, balance, threshold, blocked from " + TABLE_NAME;
        String res = "";

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();

            // while there is a next row
            while (r.next()){
                res += r.getString("name");
                res += " | ";
                res += r.getString("balance");
                res += " | ";
                res += r.getString("threshold");
                res += " | ";
                res += r.getBoolean("blocked");
                res += "\n";
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res;
    }

    public void changeBalanceByName(String name_account, int balanceModifier) {
        String query = "SELECT blocked FROM accounts WHERE name = ?;";
        boolean blocked = false;

        try (PreparedStatement s = c.prepareStatement(query)) {
            s.setString(1, name_account);
            ResultSet r = s.executeQuery();
            r.next();
            blocked = r.getBoolean(1);
            System.out.println(blocked);

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        if (!blocked){
            String query1 = "SELECT balance FROM accounts WHERE name = ?;";
            Integer bal = 0;

            try (PreparedStatement s = c.prepareStatement(query1)) {
                s.setInt(1, balanceModifier);
                ResultSet r = s.executeQuery();
                r.next();
                bal = r.getInt(1);
                System.out.println(bal);

            } catch (Exception e) {
                System.out.println(e.toString());
            }

            String query2 = "SELECT threshold FROM accounts WHERE name = ?;";
            Integer threshold = 0;

            try (PreparedStatement s = c.prepareStatement(query2)) {
                s.setInt(1, threshold);
                ResultSet r = s.executeQuery();
                r.next();
                threshold = r.getInt(1);
                System.out.println(threshold);

            } catch (Exception e) {
                System.out.println(e.toString());
            }

            if (bal + balanceModifier > threshold){
                try (Statement s = c.createStatement()) {
                    s.executeUpdate(
                            "UPDATE accounts SET balance = balance + '" + balanceModifier + "' WHERE name = '" + name_account + "';"
                    );
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }
    }

    public void blockAccount(String name_account) {
        try (Statement s = c.createStatement()) {
            s.executeUpdate(
                    "UPDATE accounts SET blocked = TRUE WHERE name = '" + name_account + "';"
            );
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
