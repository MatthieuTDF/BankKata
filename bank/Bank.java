package bank;


import java.sql.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bank {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";//on est sur MySQL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mysql?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    private static final String TABLE_NAME = "accounts";

    private Connection c;
    private static Pattern pattern;
    private static Matcher matcher;

    public Bank() {
        initDb();
    }

    private void initDb() {
        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");
            // on créé la table "account"
            try (Statement s = this.c.createStatement()) {
                s.executeUpdate("CREATE TABLE " + TABLE_NAME + "(\n" +
                        "name VARCHAR(255) NOT NULL,\n" +
                        "balance INT NOT NULL,\n" +
                        "threshold INT NOT NULL,\n" +
                        "block BOOLEAN NOT NULL DEFAULT false,\n" +   // Account not blocked by default
                        "PRIMARY KEY (name))"); //obligé de le faire là parce que sinon il y a des problèmes
                System.out.println("Table 'accounts' created successfully");
            } catch (Exception e) {//on affiche s'il y a un pb avec la création de la table
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

    void dropAllTables() { //on enlève la table "account"
        try (Statement s = c.createStatement()) {
            s.executeUpdate(
                    "DROP TABLE " + TABLE_NAME);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void createNewAccount(String name, int balance, int threshold) {
        Pattern patterName = Pattern.compile("([a-zA-Z]*([ ]|-)?)*");
        if (patterName.matcher(name).matches()) {  // si on a une bonne syntax
            Account account = verifyAccount(name);
            if (account == null) {  //s'il n'y a pas de compte avec ce nom
                if (threshold <= 0) { // le seuil ne peut pas être positif
                    String sql = "INSERT INTO " + TABLE_NAME + " (name,balance,threshold) " +
                            "VALUES ('" + name + "','" + balance + "','" + threshold + "')";
                    try (Statement s = c.createStatement()) {
                        s.executeUpdate(sql);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                } else System.out.println("Threshold exceed 0");//le seuil est positif
            }
            else
                System.out.println("The name : " +name+" already used");//le nom est déjà utilisé
        } else
            System.out.println("The name doesn't respect syntax");//pas de bonne syntax
    }

    public String printAllAccounts() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        StringBuilder accounts = new StringBuilder();
        try (Statement s = c.createStatement()) {
            ResultSet r = s.executeQuery(sql);
            while (r.next()) {
                //on créé un objet "compte"
                accounts.append((new Account(r.getString(1), r.getInt(2), r.getInt(3), r.getBoolean(4))).toString());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return accounts.toString();
    }

    public void changeBalanceByName(String name, int balanceModifier) {
        String sql2 = "UPDATE " + TABLE_NAME + " SET balance = balance + " + balanceModifier + " WHERE name = '" + name + "'";
        try (Statement s = c.createStatement()) {
            Account account = verifyAccount(name);
            if (account != null) {
                if (!account.isBlock() && balanceModifier >= account.getThreshold()) //le compte n'est pas bloqué et et que le modificateur de balance n'est pas en dessous du seuil
                    s.executeUpdate(sql2); //mise à jour de la "balance"
            } else
                System.out.println("No account with the name : " + name);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void blockAccount(String name) {
        String sql = "UPDATE " + TABLE_NAME + " SET block = 1 WHERE name = '" + name + "'";
        try (Statement s = c.createStatement()) {
            if (verifyAccount(name) != null) s.executeUpdate(sql);
            else
                System.out.println("No account with the name : " + name);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public Account verifyAccount(String name) {
        String sql1 = "SELECT * FROM " + TABLE_NAME + " WHERE name = '" + name + "'";
        try (Statement s = c.createStatement()) {
            ResultSet set = s.executeQuery(sql1);
            if (set.next()) return new Account(set.getString(1),
                    set.getInt(2),
                    set.getInt(3),
                    set.getBoolean(4));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    String getTableDump() {
        String query = "select * from " + TABLE_NAME;
        StringBuilder res = new StringBuilder();

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();

            int nbColumns = r.getMetaData().getColumnCount();

            while (r.next()){
                String[] currentRow = new String[nbColumns];
                
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