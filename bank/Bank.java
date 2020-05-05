package bank;


import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bank {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mysql?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private Connection c;
    private static final String Accounts_Table = "accounts";  // Stock the name of the Table
    private static Pattern pattern;
    private static Matcher matcher;

    public Bank() {
        initDb();
    }

    private void initDb() {
        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");
            // Create the accounts table
            try (Statement s = this.c.createStatement()) {
                s.executeUpdate("CREATE TABLE " + Accounts_Table + "(\n" +
                        "name VARCHAR(255) NOT NULL,\n" +
                        "balance INT NOT NULL,\n" +
                        "threshold INT NOT NULL,\n" +
                        "block BOOLEAN NOT NULL DEFAULT false,\n" +   // Account not blocked by default
                        "PRIMARY KEY (name))");
                System.out.println("Table 'accounts' created successfully");
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
        // Delete the accounts table
        try (Statement s = c.createStatement()) {
            s.executeUpdate(
                    "DROP TABLE " + Accounts_Table);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void createNewAccount(String name, int balance, int threshold) {
        Pattern patterName = Pattern.compile("([a-zA-Z]*([ ]|-)?)*");
        if (patterName.matcher(name).matches()) {  // if name syntax is valid
            Account account = verifyAccount(name);
            if (account == null) {  // if name not already used
                if (threshold <= 0) { // threshold can't be positive
                    String sql = "INSERT INTO " + Accounts_Table + " (name,balance,threshold) " +
                            "VALUES ('" + name + "','" + balance + "','" + threshold + "')";
                    try (Statement s = c.createStatement()) {
                        s.executeUpdate(sql);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                } else System.out.println("Threshold can't be superior to 0");
            }
            else
                System.out.println("The name : " +name+" is already used");
            } else
                System.out.println("The name doesnt respect syntax");
    }

    public String printAllAccounts() {
        String sql = "SELECT * FROM " + Accounts_Table + " ORDER BY name ASC";
        StringBuilder accounts = new StringBuilder();
        try (Statement s = c.createStatement()) {
            ResultSet r = s.executeQuery(sql);
            while (r.next()) {
                //Creating Account object
                accounts.append((new Account(r.getString(1), r.getInt(2), r.getInt(3), r.getBoolean(4))).toString());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return accounts.toString();
    }

    public void changeBalanceByName(String name, int balanceModifier) {
        String sql2 = "UPDATE " + Accounts_Table + " SET balance = balance + " + balanceModifier + " WHERE name = '" + name + "'";
        try (Statement s = c.createStatement()) {
            Account account = verifyAccount(name);
            if (account != null) {
                if (!account.isBlock() && balanceModifier >= account.getThreshold()) // if account not blocked and balanceModifier not < to the threshold
                    s.executeUpdate(sql2); // Update the field balance
            } else
                System.out.println("No account with the name : " + name);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void blockAccount(String name) {
        String sql = "UPDATE " + Accounts_Table + " SET block = 1 WHERE name = '" + name + "'";
        try (Statement s = c.createStatement()) {
            if (verifyAccount(name) != null) s.executeUpdate(sql);
            else
                System.out.println("No account with the name : " + name);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public Account verifyAccount(String name) {
        String sql1 = "SELECT * FROM " + Accounts_Table + " WHERE name = '" + name + "'";
        try (Statement s = c.createStatement()) {
            ResultSet set = s.executeQuery(sql1);
            // if the account exist
            if (set.next()) return new Account(set.getString(1),
                    set.getInt(2),
                    set.getInt(3),
                    set.getBoolean(4));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

}
