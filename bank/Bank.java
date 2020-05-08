package bank;


import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Bank {
    private static Scanner s = new Scanner(System.in);
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_kata?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private static final String TABLE_NAME = "accounts";

    private Connection c;

    public Bank() {
        initDb();
    }

    private void initDb() {
        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");
            
            String sqlCreateTable = "CREATE TABLE " + TABLE_NAME + " (`name` VARCHAR(255) NOT NULL , `balance` INT NOT NULL , `threshold` INT NOT NULL , `blocked` CHAR NOT NULL DEFAULT 'f') ENGINE = InnoDB;";
            try (Statement s = c.createStatement()) {
                s.executeUpdate(sqlCreateTable);
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
        try (Statement s = c.createStatement()) {
            s.executeUpdate("DROP TABLE accounts;");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void promptNewAccount(){
        String accountName;
        String accountBalance;
        String accountThreshold;

        System.out.println("Saisissez le nom du compte");
        accountName = s.nextLine();

        // Tant que le solde n'est pas un chiffre
        do{
            System.out.println("Saisissez le solde du compte (Chiffre)");
            accountBalance = s.nextLine();
        }while(!Pattern.matches("-?[0-9]+",accountBalance));

        // Tant que le découvert autorisé n'est pas un chiffre négatif
        do{
            System.out.println("Saisissez le découvert autorisé du compte (Chiffre négatif)");
            accountThreshold = s.nextLine();
        }while(!Pattern.matches("-[0-9]+",accountThreshold));

        // On crée le compte à la fin
        this.createNewAccount(accountName,Integer.parseInt(accountBalance),Integer.parseInt(accountThreshold));
    }

    public void createNewAccount(String accountName, int accountBalance, int accountThreshold) {
        if (accountThreshold <= 0){
            String sqlRequest = "INSERT INTO `accounts` (`name`, `balance`, `threshold`) VALUES ('" + accountName + "','" + accountBalance + "','" + accountThreshold + "')";
            try (Statement s = c.createStatement()) {
                s.executeUpdate(sqlRequest);

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    public String printAllAccounts() {
        ArrayList<Account> accounts = new ArrayList<>();
        String query = "select name,balance,threshold,blocked from " + TABLE_NAME;

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();

            // Getting nb colmun from meta data
            int nbColumns = r.getMetaData().getColumnCount();

            // while there is a next row
            int j = 0;
            while (r.next()){
                String[] currentRow = new String[nbColumns];
                // For each column in the row
                for (int i = 1 ; i <= nbColumns ; i++) {
                    currentRow[i - 1] = r.getString(i);
                }

                boolean blocked = !currentRow[3].equals("f");
                accounts.add(new Account(currentRow[0],Integer.parseInt(currentRow[1]),Integer.parseInt(currentRow[2]),blocked));
                j++;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String res = "";
        for (Account account : accounts) {
            res += account.getName() + " | " + account.getBalance() + " | " + account.getThreshold() + " | " + account.isBlocked() + "\n";
        }

        return res;

    }

    public void promptBlockAccount(){
        System.out.println("Quel compte souhaitez-vous bloquer ?");
        String accountName = promptAccount();
        blockAccount(accountName);
    }

    public void promptChangeBalance(){
        String accountName = promptAccount();
        String amount;

        do{
            System.out.println("Saissisez le montant à ajouter ou à retirer");
            amount = s.nextLine();
        }while(!Pattern.matches("-?[0-9]+",amount));

        this.changeBalanceByName(accountName,Integer.parseInt(amount));

    }

    public void changeBalanceByName(String name, int balanceModifier) {
        Account account = getAccountByName(name);
        account.modifyBalance(balanceModifier);
        persistAccount(account);
    }

    public void blockAccount(String name) {
        Account account = getAccountByName(name);
        account.setBlocked(true);
        persistAccount(account);
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

    private Account getAccountByName(String name){
        String query = "SELECT name,balance,threshold,blocked FROM " + TABLE_NAME + " WHERE name = '" + name + "'";
        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();
            int nbColumns = r.getMetaData().getColumnCount();
            r.next();
            String[] currentRow = new String[nbColumns];
            for (int i = 1 ; i <= nbColumns ; i++) {
                currentRow[i - 1] = r.getString(i);
            }
            boolean blocked = !currentRow[3].equals("f");
            return new Account(currentRow[0],Integer.parseInt(currentRow[1]),Integer.parseInt(currentRow[2]),blocked);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Account("false",0,0,true);
        }
    }

    private void persistAccount(Account account){
        String blocked = account.isBlocked() ? "t":"f";
        String sqlRequest = "UPDATE " + TABLE_NAME + " SET balance = " + account.getBalance() + ",threshold = " + account.getThreshold() + ",blocked = '" + blocked + "' WHERE name = '" + account.getName() + "'";
        try (Statement s = c.createStatement()) {
            s.executeUpdate(sqlRequest);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private String promptAccount(){
        Account account;
        String accountName;
        do{
            System.out.println("Saisissez le nom du compte");
            accountName = s.nextLine();
            account = getAccountByName(accountName);
        }while(account.getName().equals("false"));

        return accountName;
    }
}
