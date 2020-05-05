package bank;


import java.sql.*;
import java.util.Arrays;

public class Bank {

    /*
        Strings de connection à la base postgres
    */
   // private static final String JDBC_DRIVER = "org.postgresql.Driver";
    //private static final String DB_URL = "jdbc:postgresql://localhost:5439/postgres";
   // private static final String DB_USER = "postgres";

    /*
        Strings de connection à la base mysql, à décommenter et compléter avec votre nom de bdd et de user
     */
     private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
     private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
     private static final String DB_USER = "root";

    private static final String DB_PASS = "";

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
            String query = "CREATE TABLE `bank_db`.`"+ TABLE_NAME + "` ( `name` VARCHAR(255) NOT NULL , `balance` INT NOT NULL , `threshold` INT NOT NULL , `blocked` VARCHAR(255) NOT NULL )";
            PreparedStatement state = c.prepareStatement(query);
            state.executeUpdate();

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
                       "DROP table "+TABLE_NAME);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    public void createNewAccount(String name, int balance, int threshold) {
        if(balance > threshold)
        {
            try {
                String query = "INSERT INTO accounts values(?,?,?,\"false\")";
                PreparedStatement state = c.prepareStatement(query);
                state.setString(1, name);
                state.setInt(2, balance);
                state.setInt(3, threshold);
                state.executeUpdate();

                state.close();

            } catch (Exception e) {
                System.out.println("Could not create a new account");
                e.printStackTrace();//affiche l'exception et l'état de la pile d'exécution au moment de son appel
            }
        }
    }

    public String printAllAccounts() {
        String s = "";
        try
        {
            PreparedStatement statement = c.prepareStatement("SELECT * FROM accounts");

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                System.out.println("oui");
                for (int i = 1; i <= 4; i++) {
                    if(i < 4)
                        s += result.getObject(i) + " | ";
                    else
                        s += result.getObject(i);

                }
                s+="\n";
            }
            statement.close();
        }
        catch (Exception e)
        {
            System.out.println("Could not print all acounts");
            e.printStackTrace();//affiche l'exception et l'état de la pile d'exécution au moment de son appel
        }
    return s;
    }

    public void changeBalanceByName(String name, int balanceModifier) {
        try
        {
            String q  = "SELECT balance,threshold,blocked FROM accounts where name = ?";
            PreparedStatement s = c.prepareStatement(q);
            s.setString(1,name);
            ResultSet res = s.executeQuery();
            if(res.next())
            {
                int balance = res.getInt(1);
                int threshold = res.getInt(2);
                String blocked = res.getString(3);
                if(balance + balanceModifier >= threshold && !blocked.equals("true") )
                {
                    String query = "UPDATE accounts SET balance = ? WHERE name = ?";
                    PreparedStatement state = c.prepareStatement(query);
                    state.setInt(1, balance+balanceModifier);
                    state.setString(2, name);
                    state.executeUpdate();

                    state.close();
                }
            }
            s.close();

        }
        catch (Exception e)
        {
            System.out.println("Could not create a new account");
            e.printStackTrace();//affiche l'exception et l'état de la pile d'exécution au moment de son appel
        }
    }

    public void blockAccount(String name) {
        try
        {
            String query = "UPDATE accounts SET blocked = \"true\" WHERE name = ?";
            PreparedStatement state = c.prepareStatement(query);
            state.setString(1,name);
            state.executeUpdate();

            state.close();

        }
        catch (Exception e)
        {
            System.out.println("Could not create a new account");
            e.printStackTrace();//affiche l'exception et l'état de la pile d'exécution au moment de son appel
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
            System.out.println("yup" + nbColumns);
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
