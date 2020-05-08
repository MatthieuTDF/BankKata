package bank;

//Imports
import java.sql.*;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Bank {

    /*
        Strings de connection à la base mysql
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

    // Initialization of the database
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

    // Create the accounts table
    private void createTable(){

        try (Statement s = this.c.createStatement()) {
            s.executeUpdate("CREATE TABLE " + Bank.TABLE_NAME + "(\n" +
                    "name VARCHAR(255) NOT NULL,\n" +
                    "balance INT NOT NULL,\n" +
                    "threshold INT NOT NULL,\n" +
                    "locked BOOLEAN NOT NULL DEFAULT false,\n" +   // Account not blocked by default
                    "PRIMARY KEY (name))");
            System.out.println("Table 'accounts' was created successfully");

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    // Close the database
    public void closeDb() {
        try {
            c.close();
        } catch (SQLException e) {
            System.out.println("Could not close the database : " + e);
        }
    }

    // Drop the accounts table
    void dropAllTables() {
        // Delete the accounts table
        try (Statement s = this.c.createStatement()) {
            s.executeUpdate(
                    "DROP TABLE " + TABLE_NAME);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    // Creates new account
    public void createNewAccount(String name, int balance, int threshold) {
        Pattern p = Pattern.compile("([a-zA-Z]*([ ]|-)?)*");
        if (p.matcher(name).matches()) {                                        // if name syntax is valid
            Account account = validAccount(name);
            if (account == null) {                                              // if account doesn't exist
                if (threshold <= 0) {                                           // threshold can't be positive
                    String sql = "INSERT INTO " + TABLE_NAME + " (name, balance, threshold) " +
                            "VALUES ('" + name + "','" + balance + "','" + threshold + "')";

                    try (Statement s = c.createStatement()) {
                        s.executeUpdate(sql);
                        System.out.println("Account created");
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }

                } else { System.out.println("Threshold can't be superior to 0"); }
            } else { System.out.println("The name : " + name + " is already used"); }
        } else { System.out.println("The name doesnt respect syntax"); }
    }

    // Show all the accounts
    public String printAllAccounts() {
        String query = "SELECT * FROM " + TABLE_NAME;
        StringBuilder accounts = new StringBuilder();
        try (Statement s = c.createStatement()) {
            ResultSet r = s.executeQuery(query);
            while (r.next()) {                                                      //Creating Account object
                accounts.append((new Account(r.getString(1),
                        r.getInt(2),
                        r.getInt(3),
                        r.getBoolean(4))).toString());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return accounts.toString();
    }

    // Make Balance changes to account with name and modifier
    public void changeBalanceByName(String name, int balanceModifier) {             //change the balance of an account
        String query = "UPDATE " + TABLE_NAME + " SET balance = balance + "
                + balanceModifier + " WHERE name = '" + name + "'";

        try (Statement s = this.c.createStatement()) {
            Account account = validAccount(name);

            if (account != null) {
                if (!account.isLocked()) {                                          // if account not blocked
                    if (balanceModifier >= account.getThreshold()) {                //if  balanceModifier not < to the threshold
                        s.executeUpdate(query);                                     // Update the field balance
                    } else { System.out.println("Treshold hit"); }
                } else { System.out.println("Account is locked"); }
            } else { System.out.println("No account with the name : " + name); }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    // Block an account with his name
    public void blockAccount(String name) {
        String sql = "UPDATE " + TABLE_NAME + " SET locked = 1 WHERE name = '" + name + "'";

        try (Statement s = c.createStatement()) {
            if (validAccount(name) != null) { s.executeUpdate(sql); }
            else { System.out.println("No account with the name : " + name); }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    // Check if there isn't already an account with this name
    public Account validAccount(String name) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE name = '" + name + "'";
        try (Statement s = this.c.createStatement()) {
            ResultSet set = s.executeQuery(query);

            if (set.next()) {                                                           // if the account exist
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