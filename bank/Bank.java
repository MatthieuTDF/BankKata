package bank;

import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;


public class Bank {

    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5439/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "1234";
    private static final String TABLE_NAME = "ACCOUNT";

    private static final Scanner s = new Scanner(System.in);

    private Connection c;

    public Bank() {
        initDb();
        createAccountTable();

    }

    private void initDb() {
        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");

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



    void createAccountTable() {
        try (Statement statement = c.createStatement()) {
            statement.executeUpdate("CREATE TABLE " + TABLE_NAME + "(\n" +
                    "name VARCHAR(120) NOT NULL,\n" +
                    "balance INT NOT NULL,\n" +
                    "threshold INT NOT NULL,\n" +
                    "locked BOOLEAN NOT NULL)");
        } catch (Exception e) {
            System.out.println(e.toString());
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

                try (Statement statement = c.createStatement()) {
                    if(threshold <=0) {
                        statement.executeUpdate("INSERT INTO " + TABLE_NAME + " " +
                                "(name, balance, threshold, locked) " +
                                "VALUES " +
                                "('" + name + "','" + balance + "','" + threshold + "',false)");
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }


    }

    public String printAllAccounts() {
        String query = "SELECT name,balance,threshold,locked " +
                "FROM " + TABLE_NAME;

        String accountsDisplay= "";

        try (PreparedStatement statement = c.prepareStatement(query)) {
            ResultSet result = statement.executeQuery();

                while (result.next()) {
                    accountsDisplay += (new Account(
                            result.getString(1), result.getInt(2),
                            result.getInt(3), result.getBoolean(4)
                    )).toString();

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return accountsDisplay;
    }



    public void changeBalanceByName(String name, int balanceModifier) {

        Integer balance = 0 ;
        Boolean isLocked = false ;
        Integer threshold = 0 ;
      String query = "SELECT balance,locked,threshold " +
              "FROM " + TABLE_NAME + " WHERE name = '"+name+"'";

        try (PreparedStatement statement = c.prepareStatement(query,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE)) {
            ResultSet result = statement.executeQuery();

            if ( result.first() ) {
                balance = result.getInt(1);
                isLocked =result.getBoolean(2) ;
                threshold = result.getInt(3) ;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if(isLocked == false) {

            Integer newBalance = balance + balanceModifier;
            if(newBalance >= threshold) {
                try (Statement statement = c.createStatement()) {
                    statement.executeUpdate("UPDATE " + TABLE_NAME + " SET " +
                            " balance = " + newBalance + " WHERE name = '" + name + "'");
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }
    }



    public void blockAccount(String name) {
        try (Statement s = c.createStatement()) {
            s.executeUpdate("UPDATE " + TABLE_NAME + " SET " +
                    " locked = true WHERE name = '"+name+"'");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    // For testing purpose
    String getTableDump() {
        String query = "SELECT * FROM " + TABLE_NAME;
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