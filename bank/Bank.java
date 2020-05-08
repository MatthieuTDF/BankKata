package bank;


import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bank {

    private static final String TABLE_NAME = "accounts";
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5439/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "1234";

    private Connection c;

    public Bank() {
        initDb();
        if (!checkDataBase()){
            createDataBase();
        }
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

    private boolean checkDataBase(){
        String q = "SELECT EXISTS (" +
                "SELECT 1 " +
                "FROM pg_tables " +
                "WHERE schemaname = 'public'" +
                "AND tablename = '" + TABLE_NAME + "')" ;

        try (PreparedStatement s = c.prepareStatement(q)) {
            ResultSet r = s.executeQuery();
            r.next();
            return r.getBoolean(1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private void createDataBase(){
        try (Statement s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE " + TABLE_NAME + "(" +
                    "name VARCHAR(120) NOT NULL," +
                    "balance INT NOT NULL," +
                    "threshold INT NOT NULL," +
                    "locked BOOLEAN NOT NULL DEFAULT false, " +
                    "PRIMARY KEY (name))");
            System.out.println("Table 'accounts' created successfully");
        } catch (Exception e) {
            System.out.println(e.toString());
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


    }

    public String printAllAccounts() {
        // TODO

        return "";
    }

    public void changeBalanceByName(String name, int balanceModifier) {
        // TODO
    }

    public void blockAccount(String name) {
        // TODO
    }
}
