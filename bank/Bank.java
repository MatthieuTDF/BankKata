package bank;


import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bank {

    private static final String TABLE_NAME = "accounts";
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3308/bank_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

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
                    "fullName VARCHAR(120) NOT NULL," +
                    "amount INT NOT NULL," +
                    "roof INT NOT NULL," +
                    "isLocked BOOLEAN NOT NULL DEFAULT false, " +
                    "PRIMARY KEY (fullName))");
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
        //Deletes the table accounts
        try (Statement s = c.createStatement()) {
            s.executeUpdate(
                       "DROP TABLE" + TABLE_NAME);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private Account checkNewAcount(String fullName){
        Account account;
        String q = "SELECT * FROM " + TABLE_NAME + " WHERE fullName='"+ fullName +"';";
        try (PreparedStatement s = c.prepareStatement(q)){
            ResultSet r = s.executeQuery();
            if(r.next()){
                return new Account(r.getString(1), r.getInt(2), r.getInt(3), r.getBoolean(4));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }


    public void createNewAccount(String fullName, int amount, int roof) {
        // TODO
        Pattern patterFullName = Pattern.compile("([a-zA-Z]*([ ]|-)?)*");

        Matcher matcher = patterFullName.matcher(fullName);
        if (checkNewAcount(fullName) == null) {
            if (matcher.matches()) {
                if (roof <= 0) {
                    try (Statement s = c.createStatement()) {
                        s.executeUpdate(
                                " INSERT INTO " + TABLE_NAME + "(name, amount, roof, isLocked) " +
                                        "VALUES ('" + fullName + "', '" + amount + "', '" + roof + "', false)");
                    } catch (Exception e) {
                        System.out.println(e.toString());

                    }
                } else {
                    System.out.println("The roof has to be superior to '0'.");
                }

            }
            System.out.println("The name does not respect our policy");
        }
        System.out.println("The account already exists");
    }

    public String printAllAccounts() {
        // TODO
        String q = "select * from " + TABLE_NAME;
        StringBuilder total = new StringBuilder();

        try (PreparedStatement s = c.prepareStatement(q)) {
            ResultSet r = s.executeQuery();
            while (r.next()){
                total.append((new Account(r.getString(1), r.getInt(2), r.getInt(3), r.getBoolean(4))).toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return total.toString();
    }

    public void changeBalanceByName(String fullName, int balanceModifier) {
        // TODO
        Account account = checkNewAcount(fullName);
        if (account != null) {
            account.wireAmount(balanceModifier);
            try (Statement s = c.createStatement()) {
                s.executeUpdate(
                        "UPDATE " + TABLE_NAME + "SET " +
                                "amount =" + account.getAmount() + "WHERE fullName = '"+fullName+"'");
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }else{
            System.out.println("This account does not exist");
        }
    }

    public void blockAccount(String fullName) {
        // TODO
        try (Statement s = c.createStatement()) {
            s.executeUpdate("UPDATE " + TABLE_NAME + " SET " +
                    " islocked = true WHERE fullName = '"+fullName+"'");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
