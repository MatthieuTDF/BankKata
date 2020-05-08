package bank;


import java.sql.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bank {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:8889/mysql?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";
    private static final String TABLE_NAME = "compte";

    private Connection connect;

    public Bank() {
        initDb();
    }

    private void initDb() {
        try {
            Class.forName(JDBC_DRIVER);
            connect = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("bdd Ouverte");

            try (Statement sta = this.connect.createStatement()) {
                sta.executeUpdate("CREATE TABLE " + TABLE_NAME + "(\n" +
                        "name VARCHAR(255) NOT NULL,\n" +
                        "argent INT NOT NULL,\n" +
                        "decouvert INT NOT NULL,\n" +
                        "test BOOLEAN NOT NULL DEFAULT false,\n" +
                        "PRIMARY KEY (name))");
                System.out.println("la Table à bien été créée");
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
            connect.close();
        } catch (SQLException e) {
            System.out.println("impossible de fermer la bdd : " + e);
        }
    }

    void dropAllTables() {
        try (Statement sta = connect.createStatement()) {
            sta.executeUpdate(
                    "DROP TABLE " + TABLE_NAME);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public Account CheckAccount(String name) {
        String resquest1 = "SELECT * FROM " + TABLE_NAME + " WHERE name = '" + name + "'";
        try (Statement sta = connect.createStatement()) {
            ResultSet set = sta.executeQuery(resquest1);
            if (set.next()) return new Account(set.getString(1),
                    set.getInt(2),
                    set.getInt(3),
                    set.getBoolean(4));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public void createNewAccount(String name, int argent, int test) {
        Pattern Name = Pattern.compile("([a-zA-Z]*([ ]|-)?)*");
        if (Name.matcher(name).matches()) {
            Account account = CheckAccount(name);
            if (account == null) {
                if (argent <= 0) {
                    String request = "INSERT INTO " + TABLE_NAME + " (name,argent,test) " +
                            "VALUES ('" + name + "','" + argent + "','" + test + "')";
                    try (Statement s = connect.createStatement()) {
                        s.executeUpdate(request);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                } else System.out.println("le découvert ne peut être supérieur à 0 ");
            }
            else
                System.out.println("ce nom : " +name+" est existe déjà.");
        }
    }

    public String printAllAccounts() {
        String resquest = "SELECT * FROM " + TABLE_NAME;
        StringBuilder compte = new StringBuilder();
        try (Statement sta = connect.createStatement()) {
            ResultSet resultSet = sta.executeQuery(resquest);
            while (resultSet.next()) {
                compte.append((new Account(resultSet.getString(1), resultSet.getInt(2), resultSet.getInt(3), resultSet.getBoolean(4))).toString());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return compte.toString();
    }

    public void changeBalanceByName(String name, int scalesModifier) {
        String request2 = "UPDATE " + TABLE_NAME + " SET scales = scales + " + scalesModifier + " WHERE noun = '" + name + "'";
        try (Statement sta = connect.createStatement()) {
            Account NewAccount = CheckAccount(name);
            if (NewAccount != null) {
                if (!NewAccount.istest() && scalesModifier >= NewAccount.getDecouvert())
                    sta.executeUpdate(request2);
            } else
                System.out.println("le compte: " + name +" n'existe pas.");

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void blockAccount(String name) {
        String request = "UPDATE " + TABLE_NAME + " SET test = 1 WHERE name = '" + name + "'";
        try (Statement sta = connect.createStatement()) {
            if (CheckAccount(name) != null) sta.executeUpdate(request);
            else
                System.out.println("le compte: " + name +" n'existe pas.");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }




    String getTableDump() {
        String query = "select * from " + TABLE_NAME;
        StringBuilder res = new StringBuilder();

        try (PreparedStatement sta = connect.prepareStatement(query)) {
            ResultSet resultSet = sta.executeQuery();

            // Getting nb colmun from meta data
            int nbColumns = resultSet.getMetaData().getColumnCount();

            // while there is a next row
            while (resultSet.next()){
                String[] currentRow = new String[nbColumns];

                // For each column in the row
                for (int i = 1 ; i <= nbColumns ; i++) {
                    currentRow[i - 1] = resultSet.getString(i);
                }
                res.append(Arrays.toString(currentRow));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res.toString();
    }

}
