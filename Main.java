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
                // TODO

                case "0":
                    System.out.println(b.printAllAccounts());
                    break;

                case "1":

                    System.out.println("Entrez le nom du compte à créer : ");
                    String name_input = s.nextLine();

                    System.out.println("Entrez le solde du compte");
                    String balance_input = s.nextLine();

                    while(Pattern.matches("\\-?\\d+", (CharSequence) balance_input)!=true)
                        {
                            System.out.println("veuillez entrer un nombre correct\n");
                            balance_input = s.nextLine();
                        }
                    int balance = Integer.parseInt(balance_input);

                    System.out.println("Entrez le découvert maximum du compte");
                    String threshold_input = s.nextLine();
                    int threshold = Integer.parseInt(threshold_input);

                    while(Pattern.matches("\\-?\\d+", (CharSequence) threshold_input)!=true || threshold > 0)
                    {
                        System.out.println("veuillez entrer un nombre correct, négatif ou nul\n");
                        threshold_input = s.nextLine();
                        threshold = Integer.parseInt(threshold_input);
                    }

                    b.createNewAccount(name_input,balance,threshold);

                    break;

                case "2":
                    System.out.println("Entrez le nom du compte a modifier");
                     name_input = s.nextLine();

                     System.out.println("Entrez le nouveau montant du solde de ce compte");
                     balance_input = s.nextLine();
                    while(Pattern.matches("\\-?\\d+", (CharSequence) balance_input)!=true)
                    {
                        System.out.println("veuillez entrer un nombre correct\n");
                        balance_input = s.nextLine();
                    }

                    b.changeBalanceByName(name_input,Integer.parseInt(balance_input));


                    break;

                case "3":
                    System.out.println("Entrez le nom du compte a bloquer");
                    name_input = s.nextLine();
                    b.blockAccount(name_input);

                    break;




            }
        }

    }
}

