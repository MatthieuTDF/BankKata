package bank;


import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.*;

public class Bank {

    /*
        Strings de connection à la base postgres
     */
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5439/postgres";
    private static final String DB_USER = "postgres";
    private static Scanner s = new Scanner(System.in);
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
        if (!checkDBTable()){
            createDBTable();
        }
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
                            "GRANT ALL ON SCHEMA public TO postgres;" +
                            "GRANT ALL ON SCHEMA public TO public;");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    // Check if the TABLE_NAME exists
    private boolean checkDBTable(){
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

    // Creation of TABLE_NAME
    private void createDBTable() {
        try (Statement s = this.c.createStatement()){
            s.executeUpdate("CREATE TABLE " + TABLE_NAME + "(\n" +
                    "name VARCHAR(120) NOT NULL,\n" +
                    "balance INT NOT NULL,\n" +
                    "overdraft INT NOT NULL,\n" +
                    "locked BOOLEAN NOT NULL,\n" +
                    "PRIMARY KEY (name))");
            System.out.println("The table " + TABLE_NAME + "was succefully  createad");

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    // We are checking if the account exists according to it's name, and if it does, we are returning its values.
    private Account checkAccount(String name){
        // Fetching data for the account having 'name'
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE name= '" + name + "';";
        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();
            if(r.next()) {
                return new Account(
                        r.getString(1),
                        r.getInt(2),
                        r.getInt(3),
                        r.getBoolean(4));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public String printAllAccounts() {
        // We want to fetch every data from the table
        String query = "SELECT * FROM " + TABLE_NAME;
        String res = "";

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();

            // As long as there is a next row :
            while (r.next()){
                res += (new Account(
                        r.getString(1),
                        r.getInt(2),
                        r.getInt(3),
                        r.getBoolean(4)
                )).toString();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res;
    }

    public boolean inputCreateNewAccount() {
        Pattern regexValidName = Pattern.compile("([a-zA-Z]*([ ]|-)?)*");
        Pattern regexValidBalance = Pattern.compile("[1-9][0-9]*");
        Pattern regexValidThreshold = Pattern.compile("(-[1-9][0-9]*|0)");

        System.out.println("Enter the name of the account please.");
        String name = s.nextLine();

        // We check if the account already exists
        if(checkAccount(name) == null){
            Matcher matchName = regexValidName.matcher(name);

            // We check if the name is in a valid format
            if(matchName.matches()){
                System.out.println("Enter the balance of the account please.");
                String balance = s.nextLine();
                Matcher matchBalance = regexValidBalance.matcher(balance);

                // We check if the balance is in a valid format
                if(matchBalance.matches()){
                    System.out.println("Enter the threshold of the account please.");
                    String threshold = s.nextLine();
                    Matcher matchThreshold = regexValidThreshold.matcher(threshold);
                    int thresholdInt = Integer.parseInt(threshold);

                    // We check if the threshold is in a valid format
                    if(matchThreshold.matches() && thresholdInt <= 0){
                        createNewAccount(name, Integer.parseInt(balance), thresholdInt);

                    } else {
                        System.out.println("Invalid threshold");
                    }

                } else {
                    System.out.println("Invalid Balance");
                }

            } else {
                System.out.println("Invalid name");
            }

        } else {
            System.out.println("An account with this name already exists.");
        }

        return false;
    }

    public void createNewAccount(String name, int balance, int threshold) {
        if (threshold <= 0) {
            try (Statement s = this.c.createStatement()) {
                s.executeUpdate("INSERT INTO " +
                        TABLE_NAME +
                        "(name, balance, overdraft, locked)" +
                        "VALUES ('" + name + "','" + balance + "','" + threshold + "', false)");
                System.out.println("Your account has been created.");

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    public boolean inputChangeBalanceByName() {
        Pattern regexValidBalance = Pattern.compile("(-)?[1-9][0-9]*");
        System.out.println("Enter the name of the account please.");
        String name = s.nextLine();
        Account accountToChange = checkAccount(name);

        // We check if the account exists
        if(accountToChange != null) {

            // We check if the account is blocked
            if(accountToChange.isBlocked() == false) {
                System.out.println("Enter the new balance of the account please.");
                String balance = s.nextLine();
                Matcher matchBalance = regexValidBalance.matcher(balance);
                int balanceInt = Integer.parseInt(balance);

                // We check if the balance is correct
                if (matchBalance.matches() && balanceInt >= accountToChange.getThreshold()) {
                    changeBalanceByName(name, balanceInt);

                } else {
                    System.out.println("There is a problem in the balance.");
                }

            } else {
                System.out.println("Your account is blocked");
            }

        } else {
            System.out.println("The name you entered doesn't have an account.");
        }

        return false;
    }

    public void changeBalanceByName(String name, int balanceModifier) {
        Account accountToChange = checkAccount(name);
        int balance = accountToChange.getBalance() + balanceModifier;

        if(accountToChange.isBlocked() == false && balance >= accountToChange.getThreshold()) {
            try (Statement s = this.c.createStatement()) {
                s.executeUpdate("UPDATE " +
                        TABLE_NAME +
                        " SET balance =" +
                        balance +
                        " WHERE name='" +
                        name +
                        "';");

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    public void inputBlockAccount(String name){
        if(checkAccount(name) != null){
            blockAccount(name);

        } else {
            System.out.println("There is no account with this name.");
        }
    }

    public void blockAccount(String name) {
        try (Statement s = c.createStatement()) {
            s.executeUpdate("UPDATE " +
                    TABLE_NAME +
                    " SET " +
                    " locked = true WHERE name = '" +
                    name +
                    "'");
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