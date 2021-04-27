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
        int amount;

        // Loop
        while (!endOfSession) {

            flushScreen();
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

                case "0":
                    System.out.println(b.printAllAccounts());
                    break;

                case "1":
                    System.out.println("\nEnter a name");
                    name = s.nextLine();
                    try{
                        System.out.println("\nEnter a balance");
                        balance = Integer.parseInt(s.nextLine());
                        System.out.println("\nEnter a threshold");
                        threshold = Integer.parseInt(s.nextLine());
                        b.createNewAccount(name, balance, threshold);
                    } catch (NumberFormatException e) {
                        System.out.println("Input is not a number!");
                    }

                    break;

                case "2":
                    System.out.println("\nEnter a name");
                    name = s.nextLine();
                    try {
                        System.out.println("\nEnter an amount");
                        amount = Integer.parseInt(s.nextLine());
                        b.changeBalanceByName(name, amount);
                    } catch (NumberFormatException e) {
                       System.out.println("Input is not a number!");
                    }
                    break;

                case "3":
                    System.out.println("\nEnter a name");
                    name = s.nextLine();
                    b.blockAccount(name);
                    break;
            }
        }

    }
}

