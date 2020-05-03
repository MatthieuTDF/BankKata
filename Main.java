import bank.Bank;

import java.util.Scanner;

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
        String name;
        String balance;
        String balance_modif;
        String threshold;



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
                case "0":
                    b.printAllAccounts();
                    break;
                case "1":
                    System.out.println("A quel nom voulez-vous créer le compte ? ");
                    name = s.nextLine();
                    System.out.println("A combien s'élève le solde lors de la créeation ? ");
                    balance = s.nextLine();
                    System.out.println("Découvert autoriser à hauter de : ");
                    threshold = s.nextLine();
                    if(b.check_input_information(name,balance,threshold))
                    {
                        b.createNewAccount(name, Integer.valueOf(balance), Integer.valueOf(threshold));
                    }
                    break;
                case "2":
                    System.out.println("Insérer votre nom de compte : ");
                    name = s.nextLine();
                    System.out.println("Combien voulez vous retirer ( mettez une valeur négative) / ajouter  : ");
                    balance_modif = s.nextLine();
                    if(b.check_input_information(name,balance_modif,"0") == true)
                    {
                        System.out.println(b.check_input_information(name,balance_modif,"0") );
                            b.changeBalanceByName(name, Integer.valueOf(balance_modif));
                    }

                    break;
                case "3":
                    System.out.println("Quel compte voulez vous bloquer ? ");
                    name = s.nextLine();
                    if(b.check_input_information(name,"0","0"))
                    {
                        b.blockAccount(name);
                    }
                    break;
                case "q":
                    endOfSession = true;
                    b.closeDb();
                    break;
                // TODO
            }
        }

    }
}

