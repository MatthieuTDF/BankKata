package bank;


import java.sql.*;
import java.util.Arrays;


public class Bank {
     private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
     private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
     private static final String DB_USER = "root";

    private static final String DB_PASS = "";

    private static final String TABLE_NAME = "accounts";

    private Connection c;

    public Bank() {
        initDb();

        // TODO

        try (Statement s = this.c.createStatement()) {
            s.executeUpdate("CREATE TABLE " + TABLE_NAME + "(\n" +
                    "name VARCHAR(255) NOT NULL,\n" +
                    "balance INT NOT NULL,\n" +
                    "threshold INT NOT NULL,\n" +
                    "status BOOLEAN NOT NULL DEFAULT false,\n" +
                    "PRIMARY KEY (name))");
            System.out.println("Table " + TABLE_NAME + " created successfully");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private Account accountExists(String name) {
        String query = "SELECT name,balance,threshold,status FROM " + TABLE_NAME + " WHERE name='" +name+"';";
        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();
            while(r.next()) {
                return new Account(r.getString(1), r.getInt(2), r.getInt(3), r.getInt(4));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
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

    void dropAllTables() {
        try (Statement s = c.createStatement()) {
            s.executeUpdate(
                            "DROP TABLE " + TABLE_NAME);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    public void createNewAccount(String name, int balance, int threshold) {

        Account newAccount = new Account(name, balance, threshold, 0);

        try(Statement statement = c.createStatement())
        {
            if (threshold <= 0)
            {
                statement.executeUpdate("INSERT INTO "+TABLE_NAME+" (name, balance, threshold, status)"+" VALUES "+" ('"+ newAccount.getName() + "', '"+ newAccount.getBalance() +"', '"+ newAccount.getThreshold() +"', '"+ newAccount.isLockedOut() +"')");
            }
            else
            {
                System.out.println("threshold cannot exceed 0");
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public String printAllAccounts()
    {
        String res = "";
        try
        {

            // SQL SELECT query
            String query = "SELECT * FROM "+TABLE_NAME;

            // create the java statement
            Statement st = c.createStatement();

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);

            // iterate through the java resultset
            while (rs.next())
            {
                res += (new Account(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)));
            }
            st.close(); // we close the statement
        }
        catch (Exception e)
        {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }

        return res;

    }

    public void changeBalanceByName(String name, int balanceModifier) {
        Account test = accountExists(name);
        if(test != null && test.isLockedOut() == 0)
        {
            System.out.println("user "+name+" exists and he's not lockedOut");

            String query = "UPDATE " + TABLE_NAME + " SET balance = balance + "
                    +balanceModifier+" WHERE name = '"+name+"'";

            try (Statement s = this.c.createStatement())
            {
                        if (balanceModifier >= test.getThreshold())
                        {
                            s.executeUpdate(query);
                        }
                        else
                        {
                            System.out.println("Treshold capacity exceeded, nothing changed");
                        }
            }
            catch (Exception e) {
                System.out.println(e.toString());
            }

        }
        else
        {
            System.out.println("user "+name+" doesn't exists on DB or is lockedOut");
        }
    }

    public void blockAccount(String name)
    {
        Account test = accountExists(name);
        String query = "UPDATE " + TABLE_NAME + " SET status = 1 "
                +" WHERE name = '"+name+"'";
        try (Statement s = c.createStatement())
        {
            if (test != null)
            {
                s.executeUpdate(query);
            }
            else
            {
                System.out.println("user "+name+" doesn't exists on DB");
            }

        }
        catch (Exception e)
        {
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
