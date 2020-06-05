import bank.Bank;

import java.util.Scanner;
import java.util.regex.Pattern;

public class Main extends Application {

    private static Scanner s = new Scanner(System.in);

    // Nettoie l'écran des prints précédents
    private static void flushScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) {

        // Init
        Bank b = new Bank();
        String name;
        String balanceTest;
        String tresholdTest;
        int balance;
        int threshold;

        /// Declaration before loop
        boolean endOfSession = false;
        String userInput;

        // Loop
        while (!endOfSession) {

            // Menu display
            System.out.println("\n\nWhat operation do you want to do ?");
            System.out.println("0. See all accounts");
            System.out.println("1. Create a new account");
            System.out.println("2. Change balance on a given account");
            System.out.println("3. Block an account");
            System.out.println("q. Quit\n");

            // Getting primary input
            userInput = s.nextLine();

            // Processing user input
            switch (userInput) {
                case "q":
                    endOfSession = true;
                    b.closeDb();
                    break;
                case "1":
                    do { // On boucle tant que le nom est pas composé uniquement de lettres
                        System.out.println("Nom : ");
                        name = s.nextLine();
                    }while(!Pattern.matches("[a-z A-Z]+",name));
                    System.out.println("Seuil : ");
                    threshold = Integer.parseInt(s.nextLine());
                    try { // Si la balance a des lettres on ne créer pas le compte
                        System.out.println("Balance : ");
                        balance = Integer.parseInt(s.nextLine());
                        b.createNewAccount(name,balance,threshold);
                    }catch (Exception e) {

                    }

                    break;
                case "0":
                    b.printAllAccounts();
                    break;
                case  "2":
                    do {
                        System.out.println("Nom : ");
                        name = s.nextLine();
                    }while(!Pattern.matches("[a-z A-Z]+",name));
                    try {
                        System.out.println("Balance : ");
                        balance = Integer.parseInt(s.nextLine());
                        b.changeBalanceByName(name,balance);
                    }catch (Exception e) {

                    }
                    break;
                case "3":
                    do {
                        System.out.println("Nom : ");
                        name = s.nextLine();
                    }while(!Pattern.matches("[a-z A-Z]+",name));
                    b.blockAccount(name);

            }
        }

    }
}

