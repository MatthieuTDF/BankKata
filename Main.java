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

        // Loop
        while (!endOfSession) {

            // Menu display
            System.out.println("\n\nWhat operation do you want to do ?");
            System.out.println("0. See all accounts");
            System.out.println("1. Create a new account");
            System.out.println("2. Change balance on a given account");
            System.out.println("3. Block an account");
            System.out.println("4. UnBlock an account");
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
                    System.out.println("\nEnter the accounts details or quit (ex: 'name:100:-100' ):");
                    String account_data = s.nextLine();
                    b.newAccountCheck(account_data);
                    break;
                case "2":
                    System.out.println("\nEnter name and modifier (ex: 'name:-100' ):");
                    String data = s.nextLine();
                    b.changeBalanceCheck(data);
                    break;
                case "3":
                    System.out.println("\nAccount to lock :");
                    String unlockedName = s.nextLine();
                    b.blockAccount(unlockedName);
                    break;
                case "4":
                    System.out.println("\nAccount to unlock : ");
                    String lockedName = s.nextLine();
                    b.unblockAccount(lockedName);
                    break;

                // TODO
            }
        }

    }
}

