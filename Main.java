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
        int balance;
        int threshold;

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
                case "0" :
                    System.out.println(b.printAllAccounts());
                    break;
                case "1" :
                    System.out.println("Account name :");
                    name = s.nextLine();
                    System.out.println("Balance :");
                    balance = s.nextInt();
                    System.out.println("Threshold :");
                    threshold = s.nextInt();
                    b.createNewAccount(name, balance, threshold);
                    break;
                case "2" :
                    System.out.println("Account name :");
                    name = s.nextLine();
                    System.out.println("Amount (positive or negative :");
                    balance = s.nextInt();
                    b.changeBalanceByName(name, balance);
                    break;
                case "3" :
                    System.out.println("Account name :");
                    b.blockAccount(s.nextLine());
                    break;
            }
        }

    }
}

