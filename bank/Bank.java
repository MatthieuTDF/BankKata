package bank;


import java.sql.*;
import java.util.Arrays;

public class Bank {

    /*
        Strings de connection à la base postgres
     */
    /*private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5439/postgres";
    private static final String DB_USER = "postgres";*/

    /*
        Strings de connection à la base mysql, à décommenter et compléter avec votre nom de bdd et de user
     */
     private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
     private static final String DB_URL = "jdbc:mysql://localhost:3306/bank";
     private static final String DB_USER = "root";

    private static final String DB_PASS = "";

    private static final String TABLE_NAME = "account";

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
                            "GRANT ALL ON SCHEMA public TO mysql;" +
                            "GRANT ALL ON SCHEMA public TO public;");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    public void createNewAccount(String name, int balance, int threshold) {
        // TODO
         Account newAccount = new Account(name, balance, threshold);

         try(Statement statement = c.createStatement()){
             if (threshold <= 0){
                 statement.executeUpdate("INSERT INTO" + TABLE_NAME + " " +
                         "(name, balance, threshold, status)"+ "VALUES" + "('"+ newAccount.get$name() + "', '"+ newAccount.get$amount() +"', '"+ newAccount.get$allowed() +"', '"+ newAccount.get$status() +"')");
             }
         }catch (Exception e){
             System.out.println(e.toString());
         }

    }

    public String printAllAccounts() {
        // TODO
        return getTableDump();

    }

    public void changeBalanceByName(String name, int balanceModifier) {
        // TODO
        try(Statement statement = c.createStatement()){

            //my bad, i have changed balance with balanceModifier instead of adding
             double balance = statement.executeUpdate("SELECT amount FROM" + TABLE_NAME + "WHERE name =" + name);


        }catch (Exception e){
            System.out.println(e.toString());
        }

        double newBalance = balance + balanceModifier;

        try(Statement statement = c.createStatement()){

                statement.executeUpdate("UPDATE" + TABLE_NAME + "SET" + "amount =" + newBalance + "WHERE name =" + name);

        }catch (Exception e){
            System.out.println(e.toString());
        }

    }

    public void blockAccount(String name) {
        // TODO
        try(Statement statement = c.createStatement()){

            statement.executeUpdate("UPDATE" + TABLE_NAME + "SET" + "status =" + 0 + "WHERE name =" + name);

        }catch (Exception e){
            System.out.println(e.toString());
        }

    }

    // For testing purpose
    String getTableDump() {
        String query = "select * from " + "account";
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
