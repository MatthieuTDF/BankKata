package bank;


import java.sql.*;
import java.util.Arrays;

public class Bank {

    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5439/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "toor";

    private static final String TABLE_NAME = "accounts";

    private Connection c;

    public Bank() {
        initDb();
        if(!checkDB()){
            createDB();
        }
        // TODO
    }

    private void initDb() {
        try {
            Class.forName(JDBC_DRIVER);
            this.c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public void closeDb() {
        try {
            this.c.close();
        } catch (SQLException e) {
            System.out.println("Could not close the database : " + e);
        }
    }

    void dropAllTables() {
        try (Statement s = this.c.createStatement()) {
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

    private boolean checkDB(){ //Checks if table TABLE_NAME exists ; returns boolean
        String query = "SELECT EXISTS (" +
                        "SELECT 1 " +
                        "FROM pg_tables " +
                        "WHERE schemaname = 'public'" +
                        "AND tablename = '" + TABLE_NAME + "')" ;

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();
            r.next();
            return r.getBoolean(1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private void createDB(){ //Creates table TABLE_NAME

        try (Statement s = this.c.createStatement()){
            s.executeUpdate("CREATE TABLE " + TABLE_NAME + "(\n" +
                                    "id INT NOT NULL,\n" +
                                    "name VARCHAR(120) NOT NULL,\n" +
                                    "balance INT NOT NULL,\n" +
                                    "overdraft INT NOT NULL,\n" +
                                    "locked BOOLEAN NOT NULL,\n" +
                                    "PRIMARY KEY (id))");
            System.out.println("Table 'accounts' created successfully");

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
