package bank;


import java.sql.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bank {

    /*
        Strings de connection à la base postgres
     */
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5439/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "1234";
    private static final String TABLE_NAME = "accounts";
    private Connection c;


    public Bank() {

        initDb();
        if (!checkDB())
            createDB();
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


    public void createNewAccount(String name, int balance, int threshold) {

        if (threshold < 1) {

            try (Statement s = this.c.createStatement()) {

                s.executeUpdate(String.format("INSERT INTO %s(name, balance, overdraft, banned)VALUES ('%s','%d','%d', false)", TABLE_NAME, name, balance, threshold));

                System.out.println("Account Created");

            } catch (Exception e) {

                System.out.println(e.toString());
            }

        } else {
            System.out.println("Incorrect entry");
        }
    }

    //returns Account object by name identification
    private Account getAccountByName(String name) {

        try (PreparedStatement s = c.prepareStatement(
                String.format("SELECT * FROM %s WHERE name='%s", TABLE_NAME, name)
        )) {

            ResultSet r = s.executeQuery();
            r.next();
            return new Account(r.getString(1), r.getInt(2), r.getInt(3), r.getBoolean(4));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    //Updates Account Object
    private void updateAccountToDB(Account ac) {

        try (Statement s = this.c.createStatement()) {
            s.executeUpdate(
                    String.format("UPDATE %s SET balance=%d, overdraft=%d,banned=%s WHERE name=%s",
                    TABLE_NAME, ac.getBalance(), ac.getOverdraft(), ac.isBanned(), ac.getName())
            );
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public String printAllAccounts() {
        String res = "";

        try (PreparedStatement s = c.prepareStatement(
                String.format("select * from %s", TABLE_NAME
        ))) {

            ResultSet r = s.executeQuery();
            while (r.next()) {
                res += (new Account(r.getString(1), r.getInt(2), r.getInt(3), r.getBoolean(4))).toString();
            }

        } catch (Exception e) {

            System.out.println(e.getMessage());
        }

        return res;
    }


    public void changeBalanceByName(String name, int balanceModifier) {

        Account ac = getAccountByName(name);
        assert ac != null;
        int ba = ac.getBalance() + balanceModifier;

        if (!ac.isBanned()) {

            if (ba > ac.getOverdraft()) {

                ac.setBalance(ba); // Sets new balance from modifier
                updateAccountToDB(ac); // Updates account in DB
                System.out.println("Account updated");

            } else System.out.println("Overdraft reached");

        } else System.out.println("Account is banned banking");
    }

    public void blockAccount(String name) {

        Pattern p = Pattern.compile("([a-z]+)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(name);

        if (m.groupCount() == 1 && m.matches()) {
            Account ac = getAccountByName(m.group(1));

            if (ac != null) {

                if (ac.isBanned()) {

                    System.out.println("Account is already banned");

                } else {

                    ac.setBanned(true);
                    updateAccountToDB(ac);
                    System.out.println("Account is now banned");
                }

            } else System.out.println("Account does not exist");

        } else System.out.println("Wrong input");
    }

    public void unblockAccount(String name) {

        Pattern p = Pattern.compile("([a-z]+)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(name);

        if (m.groupCount() == 1 && m.matches()) {
            Account ac = getAccountByName(m.group(1));

            if (ac != null) {

                if (!ac.isBanned()) {

                    System.out.println("Account is already free");
                } else {
                    ac.setBanned(true);
                    updateAccountToDB(ac);
                    System.out.println("Account is now free");
                }

            } else System.out.println("Account does not exist");

        } else System.out.println("Wrong input");
    }


    private void createDB() { //Create table TABLE_NAME

        try (Statement s = this.c.createStatement()) {

            s.executeUpdate(String.format("CREATE TABLE %s (name VARCHAR(120) NOT NULL,balance INT NOT NULL,overdraft INT NOT NULL,banned BOOLEAN NOT NULL,PRIMARY KEY (name))", TABLE_NAME));

            System.out.println("Table " + TABLE_NAME + " created successfully");

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    /////// For testing purpose ///////

    String getTableDump() {
        String query = "select * from " + TABLE_NAME;
        String res = "";

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();

            // Getting nb colmun from meta data
            int nbColumns = r.getMetaData().getColumnCount();

            // while there is a next row
            while (r.next()) {
                String[] currentRow = new String[nbColumns];

                // For each column in the row
                for (int i = 1; i <= nbColumns; i++) {
                    currentRow[i - 1] = r.getString(i);
                }
                res += Arrays.toString(currentRow);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res;
    }

    private boolean checkDB() { //Checks if TABLE_NAME exist

        try (PreparedStatement s = c.prepareStatement(

                String.format("SELECT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public'AND tablename = '%s')", TABLE_NAME)

        )) {

            ResultSet r = s.executeQuery();
            r.next();
            return r.getBoolean(1);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    // Checks if user input is correct
    public boolean changeBalanceCheck(String data) {

        Pattern p = Pattern.compile("([a-z]+):([-]?[1-9][0-9]*)", Pattern.CASE_INSENSITIVE); // "sensible à la casse"
        Matcher m = p.matcher(data);

        if (m.groupCount() == 2 && m.matches()) {
            Account ac = getAccountByName(m.group(1)); //Verify existing or not banned
            if (ac != null) {
                if (!ac.isBanned()) {

                    changeBalanceByName(ac.getName(), Integer.parseInt(m.group(2))); // If all good make changes
                    return true;

                } else System.out.println("Account is Banned");

            } else System.out.println("Account does not exist");

        } else System.out.println("Wrong input");
        return false;
    }

    // Check input from user and availability to creating an account
    public boolean newAccountCheck(String accountData) {

        Pattern p = Pattern.compile("^([a-z]+):([1-9][0-9]*):(-[1-9][0-9]*|0)$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(accountData);

        if(m.groupCount() == 3 && m.matches()){

            Account ac;
            ac = getAccountByName(m.group(1));

            if (ac == null) {
                createNewAccount(m.group(1), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3))); // Creates account
                return true;
            }else System.out.println("Account already exists");

        }else System.out.println("Wrong input");

        return false;
    }
}
