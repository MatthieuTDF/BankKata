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
            // On crée la table
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

    // Fonction qui récupère l'entrée utilisateur et ensuite l'envoie à la fonction interne de traitement pour créer le nouveau compte
    public void promptNewAccount(){
        // On crée les trois variables qui contiendront notre futur compte
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

    // Fonction interne de création d'un compte
    public void createNewAccount(String accountName, int accountBalance, int accountThreshold) {
        // On vérifie que le découvert autorisé est bien inférieur ou égal à 0
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
        // On crée un ArrayList pour stocker tous les objets account que l'on va récupérer dans la base
        ArrayList<Account> accounts = new ArrayList<>();
        String query = "select name,balance,threshold,blocked from " + TABLE_NAME;

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

                // On crée un booléen à partir de ce qu'on a récupéré dans la base, un f ou un t
                boolean blocked = !currentRow[3].equals("f");
                // On crée un nouvel objet account qu'on ajoute dans notre ArrayList
                accounts.add(new Account(currentRow[0],Integer.parseInt(currentRow[1]),Integer.parseInt(currentRow[2]),blocked));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // On déclare le string qu'on l'on va return
        String res = "";

        // Pour chaque compte contenu dans notre ArrayList
        for (Account account : accounts) {
            // On ajoute le toString de notre account à la variable de return
            res += account.toString();
        }
        return res;
    }

    // Fonction de saisie du compte pour le bloquer
    public void promptBlockAccount(){
        System.out.println("Quel compte souhaitez-vous bloquer ?");

        // On appelle la fonction interne de saisie de nom de compte
        String accountName = promptAccount();

        // On bloque le compte
        blockAccount(accountName);
    }

    // Fonction de saisie du compte pour changer sa balance
    public void promptChangeBalance(){
        // On appelle la fonction interne de saisie de nom de compte
        String accountName = promptAccount();
        String amount;

        // Tant que le montant saisi n'est pas un chiffre
        do{
            System.out.println("Saissisez le montant à ajouter ou à retirer");
            amount = s.nextLine();
        }while(!Pattern.matches("-?[0-9]+",amount));

        // On modifie la balance
        this.changeBalanceByName(accountName,Integer.parseInt(amount));

    }

    public void changeBalanceByName(String name, int balanceModifier) {
        // On récupère l'account via la méthode interne
        Account account = getAccountByName(name);

        // On modifie sa balance via une méthode de l'objet Account qui permet de faire les vérifications de découvert autorisé
        account.modifyBalance(balanceModifier);

        // On "persiste" l'objet Account en cours
        persistAccount(account);
    }

    public void blockAccount(String name) {
        // On récupère l'account via la méthode interne
        Account account = getAccountByName(name);

        // On modifie la valeur de blocage du compte à true
        account.setBlocked(true);

        // On "persiste" l'objet Account en cours
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

    // Fonction qui renvoie un objet account lorsqu'on saisit son nom
    private Account getAccountByName(String name){
        // On prépare la requête
        String query = "SELECT name,balance,threshold,blocked FROM " + TABLE_NAME + " WHERE name = '" + name + "'";
        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();
            int nbColumns = r.getMetaData().getColumnCount();
            r.next();
            String[] currentRow = new String[nbColumns];
            for (int i = 1 ; i <= nbColumns ; i++) {
                currentRow[i - 1] = r.getString(i);
            }

            // si la requête s'est bien passée on renvoie un objet account avec les résultats de la requête
            boolean blocked = !currentRow[3].equals("f");
            return new Account(currentRow[0],Integer.parseInt(currentRow[1]),Integer.parseInt(currentRow[2]),blocked);
        } catch (Exception e) {
            System.out.println("Le compte n'a pas été trouvé, veuillez saisir un compte valide");
            // Si le compte n'a pas été trouvé, on renvoie un objet Account avec false en nom et true pour bloqué
            // Je n'ai pas trouvé comment on pouvait avoir une valeur de retour mixe dans les méthodes, c'est donc la solution que j'ai trouvée,
            // même si renvoyer null, false ou -1 aurait été plus propre.
            return new Account("false",0,0,true);
        }
    }

    /* fonction qui "persiste" un objet en base, pour un objet existant on considère qu'il existe en base,
     * cette fonction permet de l'update avec les paramètres actuels de l'account.
     */
    private void persistAccount(Account account){
        String blocked = account.isBlocked() ? "t":"f";
        String sqlRequest = "UPDATE " + TABLE_NAME + " SET balance = " + account.getBalance() + ",threshold = " + account.getThreshold() + ",blocked = '" + blocked + "' WHERE name = '" + account.getName() + "'";
        try (Statement s = c.createStatement()) {
            s.executeUpdate(sqlRequest);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    // fonction de saisie du compte
    private String promptAccount(){
        Account account;
        String accountName;

        // tant qu'on a pas un compte valide on vient demander à l'utilisateur de saisir un nom de compte
        do{
            System.out.println("Saisissez le nom du compte");
            accountName = s.nextLine();
            account = getAccountByName(accountName);
        }while(account.getName().equals("false"));

        // Quand le compte a été trouvé, on le renvoie
        return accountName;
    }
}
