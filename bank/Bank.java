package bank;


import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bank {

    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5439/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "1234";

    private static final String TABLE_NAME = "accounts";

    private Connection c;
    private static Pattern pattern;
    private static Matcher matcher;

    public Bank() {
        initDb();
            if(!checkDB()){ // If Table TABLE_NAME is does not exist it creates it
                createDB(); // Creates Table TABLE_NAME
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

    //returns Account object from string name else returns null
    private Account getAccountByName(String name){
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE name='" + name +"';";
        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();
            while(r.next()) {
                return new Account(r.getString(1), r.getInt(2), r.getInt(3), r.getBoolean(4));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    //Updates Account Object in database
    private void updateAccountToDB(Account ac){
        try (Statement s = this.c.createStatement()){
            s.executeUpdate("UPDATE "+TABLE_NAME+" SET balance="+ac.getBalance()+", "+
                            "overdraft="+ac.getOverdraft()+","+
                            "locked="+ac.isLocked()+" "+
                            "WHERE name='"+ac.getName()+"';");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    // Function to check input from user and availability when creating a new account
    public boolean newAccountCheck(String accountData){
        Pattern p = Pattern.compile("^([a-z]+):([1-9][0-9]*):(-[1-9][0-9]*|0)$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(accountData);
        if(m.groupCount() == 3 && m.matches()){
            Account ac;
            ac = getAccountByName(m.group(1));
            // If account does not exists creates it
            if (ac == null) {
                createNewAccount(m.group(1), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3))); // Creates account
                return true;
            }else{System.out.println("Account already exists !");}

        }else{System.out.println("Bad input !");}

        return false;
    }

    // Creates new account
    public void createNewAccount(String name, int balance, int threshold) {
        if(threshold <= 0){ // threshold must be positif
            try (Statement s = this.c.createStatement()){
                s.executeUpdate("INSERT INTO " + TABLE_NAME +
                        "(name, balance, overdraft, locked)" +
                        "VALUES ('"+name+"','"+ balance +"','" +threshold+"', false)");
                System.out.println("Account Created !");
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }else{
            System.out.println("Overdraft incorrect !");
        }
    }

    public String printAllAccounts() {
        String query = "select * from " + TABLE_NAME; // Getting all accounts
        String res = "";

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();
            // while there is a next row Stringify the account and adds it to the result
            while (r.next()){
                //Creating Account object because why not
                res += (new Account(r.getString(1), r.getInt(2), r.getInt(3), r.getBoolean(4))).toString();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res;
    }

    // Checks if user input is correct
    public boolean changeBalanceCheck(String data){
        Pattern p = Pattern.compile("([a-z]+):([-]?[1-9][0-9]*)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(data);
        if(m.groupCount() == 2 && m.matches()) {
            Account ac = getAccountByName(m.group(1)); //Gets Object to check if Exist or Locked
            if (ac != null){
                if(ac.isLocked() != true){
                    changeBalanceByName(ac.getName(), Integer.parseInt(m.group(2))); // If all good make changes
                    return true;
                }else{ System.out.println("Account is Locked !");}

            }else{ System.out.println("Account does not exist !");}

        }else{System.out.println("Bad input !");}
        return false;
    }

    // Make Balance changes to account with name and modifier
    public void changeBalanceByName(String name, int balanceModifier) {
        Account ac = getAccountByName(name);
        int ba = ac.getBalance() + balanceModifier;
        if(ac.isLocked() != true){
            if(ba > ac.getOverdraft()){
                ac.setBalance(ba); // Sets new balance from modifier
                updateAccountToDB(ac); // Updates account in DB
                System.out.println("Account updated !");

            }else{System.out.println("Overdraft Hit !");}

        }else{ System.out.println("Account is Locked !");}

    }

    public void blockAccount(String name) {
        // TODO
        Pattern p = Pattern.compile("([a-z]+)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(name);
        if(m.groupCount() == 1 && m.matches()) {
            Account ac = getAccountByName(m.group(1));
            if(ac != null){
                if(ac.isLocked()){
                    System.out.println("Account is already Locked !");
                }else{
                    ac.setLocked(true);
                    updateAccountToDB(ac);
                    System.out.println("Account is now Locked !");
                }
            }else{ System.out.println("Account does not exist !");}

        }else{System.out.println("Bad input !");}
    }

    public void unblockAccount(String name) {
        // TODO
        Pattern p = Pattern.compile("([a-z]+)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(name);
        if(m.groupCount() == 1 && m.matches()) {
            Account ac = getAccountByName(m.group(1));
            if(ac != null){
                if(!ac.isLocked()){
                    System.out.println("Account is already Unlocked !");
                }else{
                    ac.setLocked(true);
                    updateAccountToDB(ac);
                    System.out.println("Account is now unLocked !");
                }
            }else{ System.out.println("Account does not exist !");}

        }else{System.out.println("Bad input !");}
    }

    //Checks if table TABLE_NAME exists ; returns boolean
    private boolean checkDB(){
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

    //Creates table TABLE_NAME
    private void createDB(){

        try (Statement s = this.c.createStatement()){
            s.executeUpdate("CREATE TABLE " + TABLE_NAME + "(\n" +
                                    "name VARCHAR(120) NOT NULL,\n" +
                                    "balance INT NOT NULL,\n" +
                                    "overdraft INT NOT NULL,\n" +
                                    "locked BOOLEAN NOT NULL,\n" +
                                    "PRIMARY KEY (name))");
            System.out.println("Table 'accounts' created successfully");

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    // For testing purpose
    public String getTableDump() {
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
