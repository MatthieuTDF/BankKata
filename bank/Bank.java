package bank;


import java.sql.*;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bank {

    /*
        Strings de connection à la base postgres
     */
    private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/bank_kata";
    private static final String DB_USER = "root";
    private static final String DB_NAME = "bank_kata";


    /*
        Strings de connection à la base mysql, à décommenter et compléter avec votre nom de bdd et de user
     */
    // private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    // private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    // private static final String DB_USER = "bank_user";

    private static final String DB_PASS = "";

    private static final String TABLE_NAME = "account";

    private Connection c;

    public Bank() {
        initDb();

        // TODO
    }

    private void initDb() {
        try
        {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");

            // TODO Init DB
            if (check_table() == false) {
                String query = "CREATE TABLE account (" +
                        "name varchar(255) PRIMARY KEY , " +
                        "balance int(11)," +
                        "thresold int (11)," +
                        "blocked boolean )";

                try (PreparedStatement s = c.prepareStatement(query)) {
                    ResultSet r = s.executeQuery();
                    r.next();
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    private boolean check_table() // on check si la table account existe deja
    {
        String query = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + DB_NAME + "' AND TABLE_NAME = '" + TABLE_NAME + "'";
        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();
            return r.next();  // return true si elle existe / false si elle existe pas
        }
        catch (SQLException e) {
            System.out.println( e);

            return false;
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
                         "TRUNCATE TABLE account "); // j'ai préféré vider ma table à chaque fois au lieu de la supprimer (pour les tests)
                       /*"DROP SCHEMA public CASCADE;" +
                            "CREATE SCHEMA public;" +
                            "GRANT ALL ON SCHEMA public TO postgres;" +
                            "GRANT ALL ON SCHEMA public TO public;");*/
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public boolean check_information_on_creation(String name, int balance, int threshold)
    // permet de vérifier que le solde soit positif ou égal à 0 et que le découvert soit égal ou inférieur à 0
    {
        int no_error = 0;
        if (balance < 0) {
            System.out.println("vous ne pouvez pas créer un compte avec un solde négatif!");
            no_error += 1;
        }
        if (threshold > 0)
        {
            System.out.println("vous ne pouvez pas créer un compte avec un découvert positif!");
            no_error += 1;
        }
        return  (no_error >0) ? false : true ;
    }

    public boolean check_input_information(String name, String balance, String threshold)
            // permet de vérifier les inputs dans le main a l'aide de regex
    {
        int no_error = 0;
        Pattern regex_name = Pattern.compile("[A-Z]{0,124}");
        Matcher match = regex_name.matcher(name);
        if(!match.matches()) {
            System.out.println("Le nom doit être composer uniquement de lettres majuscules!");
            no_error += 1;
        }
        Pattern regex_balance = Pattern.compile("[-]{0,1}[0-9]*");
        match = regex_balance.matcher(balance);
        if(!match.matches())
        {
            System.out.println("Le solde doit être composer uniquement de chiffres!");
            no_error += 1;
        }
        Pattern regex_threshold = Pattern.compile("[-]{0,1}[0-9]*");
        match = regex_threshold.matcher(threshold);
        if(!match.matches())
        {
            System.out.println("Le découvert doit être composer uniquement de chiffres!");
            no_error += 1;
        }

        return  (no_error >0) ? false : true ;
    }


    public void createNewAccount(String name, int balance, int threshold)
    {
        if (check_information_on_creation(name,balance,threshold)) // on vérifie dabord les informations avant de créer le compte
        {
            Account a = new Account(name, balance, threshold, false);
            String query = "INSERT INTO " + TABLE_NAME +" VALUES(?, ?, ?, false)";

            try (PreparedStatement s = c.prepareStatement(query)) {
                s.setString(1, a.getName());
                s.setInt(2, a.getBalance());
                s.setInt(3, a.getThreshold());
                s.executeQuery();
                System.out.println("Le compte de " + a.getName() + " avec un solde : " +
                        a.getBalance() + "€ et un découvert à : " + a.getThreshold() + "€ a bien été créé !" );
            }
            catch (Exception e)
            {
                System.out.println(e.toString());

            }
        }
    }

    public String printAllAccounts()
    {
        String query = "SELECT * FROM " + TABLE_NAME ;
        String print = "";
        try (PreparedStatement s = c.prepareStatement(query))
        {
            ResultSet res = s.executeQuery();

            while (res.next())
            {
                // on creer un objet avec ce qu'on récupere de la requete sql  qu'on toString afin
                // de le stocker dans une String qui sera print
                print += (new Account(res.getString(1),res.getInt(2),
                        res.getInt(3),res.getBoolean(4)).toString());
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());

        }
        System.out.println(print);
        return print;
    }

    public Account verify_existence(String name) // permet de vérifier l'existence d'un compte
            // cette fonction me permet de récupérer sous forme d'objet ma recherche sql que je vais utiliser dans d'autres fonctions
    {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE name = '" + name + "'" ;

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet res = s.executeQuery();
            if (res.next())
            {
                Account people = new Account(res.getString(1),
                        res.getInt(2),res.getInt(3),res.getBoolean(4));
                System.out.println("le comtpe existe ");
                return people; //
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());

        }
        System.out.println("le comtpe n'existe pas ");
        return null;
    }

    public boolean check_balance_modif(int modifier, int balance, int threshold,boolean locked )
            // vérifie les conditions qui nous permettent de modifier le solde
    {
        if (modifier + balance < threshold )
        {
            System.out.println("vous n'avez pas assez de fond pour retirer");
            return false;
        }
        else if (locked == true)
        {
            System.out.println("le compte est blocké");
            return false;
        }
        else
        {
            return true;
        }
    }

    public void changeBalanceByName(String name, int balanceModifier) {
        // TODO
        if(verify_existence(name) != null) // on recupere le compte ciblé sous forme d'objet
        {
            Account people = verify_existence(name); // on recupere l'objet qu'on cible
            if (!check_balance_modif(balanceModifier,people.getBalance(),people.getThreshold(),people.isState())) return;
// on impact l'ancien solde par le mouvement financier demander
            balanceModifier = balanceModifier + people.getBalance();
            String query = "UPDATE " + TABLE_NAME + " SET balance = ? WHERE name = ?";
            try (PreparedStatement s = c.prepareStatement(query)) {
                s.setString(2, name);
                s.setInt(1, balanceModifier);
                s.executeQuery();
            } catch (Exception e) {
                System.out.println(e.toString());

            }
        }

    }

    public void blockAccount(String name) {
        // TODO
        if (verify_existence(name) != null)
        {
            Account people = verify_existence(name); // on recupere le compte ciblé sous forme d'objet

            String query = "UPDATE " + TABLE_NAME + " SET blocked = true WHERE name = ?";
            try (PreparedStatement s = c.prepareStatement(query)) {
                s.setString(1, name);
                s.executeQuery();
                System.out.println("le compte a bien ete blocké");
            } catch (Exception e) {
                System.out.println(e.toString());
            }
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
