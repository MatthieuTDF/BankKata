package bank;

import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bank {

    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5439/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "1234";

    private static final String TABLE_NAME = "accounts";

    private static final Scanner s = new Scanner(System.in);

    private Connection c;

    public Bank() {
        initDb();
        // TODO
        if(!checkDBAccount()){
            createTableAccount();
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

    private boolean checkDBAccount(){ //Checks if table TABLE_NAME exists ; returns boolean
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

    void createTableAccount() {
        try (Statement s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE " + TABLE_NAME + "(\n" +
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

    private Account verifAccountExist(String name) {
        String query = "SELECT * " +
                "FROM " + TABLE_NAME + " WHERE name = '"+name+"'";

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();
            if (r.next()) {
                return new Account(
                        r.getString(1), r.getInt(2),
                        r.getInt(3), r.getBoolean(4)
                );
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void inputCreateNewAccount(){ //Récupère les input quand on choisi create new account
        Pattern regexName = Pattern.compile("([a-zA-Z]*([ ]|-)?)*"); //regex qui vérifie si le nom est conforme
        Pattern regexBalance = Pattern.compile("[1-9][0-9]*"); //regex qui vérifie si le solde est conforme
        Pattern regexThreshold = Pattern.compile("(-[1-9][0-9]*|0)"); //regex qui vérifie si le découvert est conforme

        System.out.println("Enter the name");
        String name = s.nextLine();
        if (verifAccountExist(name) == null) { //Vérifie si le compte exist
            Matcher match = regexName.matcher(name);
            if (name.length() < 120 && match.matches()) { //vérifie si la longueur et ce qui est saisi respect les conditions
                System.out.println("Enter the balance");
                String balance = s.nextLine();
                Matcher matchBalance = regexBalance.matcher(balance);
                if (matchBalance.matches()) { //vérifie si ça remplie les conditions dans le regex
                    System.out.println("Enter the threshold");
                    String threshold = s.nextLine();
                    Matcher matchThreshold = regexThreshold.matcher(threshold);
                    if (matchThreshold.matches()) { //vérifie si ça remplie les conditions dans le regex
                        createNewAccount(name, Integer.parseInt(balance), Integer.parseInt(threshold));
                    } else {
                        System.out.println("The threshold is incorrect");
                    }
                } else {
                    System.out.println("The balance is incorrect");
                }
            } else {
                System.out.println("The name is incorrect");
            }
        } else {
            System.out.println("This Account already exist");
        }
    }

    public void createNewAccount(String name, int balance, int threshold) {
        // TODO
        if (verifAccountExist(name) == null) {
            if (threshold <= 0) {
                try (Statement s = c.createStatement()) {
                    s.executeUpdate("INSERT INTO " + TABLE_NAME + " " +
                            "(name, balance, threshold, locked) " +
                            "VALUES " +
                            "('" + name + "','" + balance + "','" + threshold + "',false)");
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            } else {
                System.out.println("The threshold is incorrect");
            }
        }
    }

    public String printAllAccounts() {
        // TODO
        String query = "SELECT name,balance,threshold,locked " +
                "FROM " + TABLE_NAME;
        String res = "";

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();

            // while there is a next row
            while (r.next()){
                res += (new Account(
                        r.getString(1), r.getInt(2),
                        r.getInt(3), r.getBoolean(4)
                )).toString();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res;
    }

    public void inputChangeBalance(){
        Pattern regexBalance = Pattern.compile("(-)?[1-9][0-9]*");
        System.out.println("Enter the name");
        String name = s.nextLine();
        if (verifAccountExist(name) != null) {
            System.out.println("Enter the Balance");
            String balance = s.nextLine();
            Matcher matchBalance = regexBalance.matcher(balance);
            if (matchBalance.matches()) {
                changeBalanceByName(name, Integer.parseInt(balance));
            } else {
                System.out.println("The balance is incorrect");
            }
        } else {
            System.out.println("This Account not exist");
        }
    }

    public void changeBalanceByName(String name, int balanceModifier) { //Update de la bdd en changeant le solde du compte
        // TODO
        Account ac = verifAccountExist(name);
        if (ac != null) {
            if (ac.getLocked() == false) {
                int newBalance = ac.getBalance() + balanceModifier;
                if (newBalance >= ac.getThreshold()) {
                    ac.setBalance(newBalance);
                    try (Statement s = c.createStatement()) {
                        s.executeUpdate("UPDATE " + TABLE_NAME + " SET " +
                                " balance = '" + ac.getBalance() + "' WHERE name = '" + name + "'");
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                } else {
                    System.out.println("This balance is below the threshold");
                }
            } else {
                System.out.println("This account is locked");
            }
        } else {
            System.out.println("This Account not exist");
        }
    }

    public void inputBlockAccount(){
        System.out.println("Enter the name");
        String name = s.nextLine();
        if (verifAccountExist(name) != null) {
            blockAccount(name);
        } else {
            System.out.println("This Account not exist");
        }
    }

    public void blockAccount(String name) { //Update de la bdd en bloquant le compte
        // TODO
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
