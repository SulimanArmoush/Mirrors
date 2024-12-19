import java.util.Scanner;

public class Game {
    private GameBoard gameBoard;
    private UserPlay userPlay;

    private BFS bfs;
    private DFS dfs;
    private UCS ucs;
    private HC hc;
    private int key;

    public void showInstruction(String message) {
        System.out.println();
        System.out.println(message);
    }

    public void showSuccessMessage(String message) {
        System.out.println();
        System.out.println("\u001B[32m" + message + "\u001B[0m"); // Green text
    }

    public void showErrorMessage(String message) {
        System.out.println();
        System.out.println("\u001B[31m" + message + "\u001B[0m"); // Red text
    }

    public void startGame() {
        Scanner scanner = new Scanner(System.in);

        showInstruction("\u001B[36mEnter the level (1, 2, ... 10):\u001B[0m");
        System.out.print("Level: ");

        if (scanner.hasNextInt()) {
            key = scanner.nextInt();
            scanner.nextLine();

            if (key >= 1 && key <= 10) {
                try {
                    gameBoard = new GameBoard("Level" + key, "files/levelsConfig.json");
                    userPlay = new UserPlay(gameBoard);
                    bfs = new BFS(gameBoard);
                    dfs = new DFS(gameBoard);
                    ucs = new UCS(gameBoard);
                    hc = new HC(gameBoard);
                } catch (Exception e) {
                    showErrorMessage("Error: " + e.getMessage());
                }
                playGame(scanner);

            } else {
                showErrorMessage("Invalid input. Please enter a number between 1 and 10:");
            }
        } else {
            showErrorMessage("Invalid input. Please enter a valid number:");
            scanner.nextLine();
        }
    }


    public void playGame(Scanner scanner) {
        showInstruction(
                "\u001B[33m1 - User Play\n"
                        + "2 - BFS\n"
                        + "3 - DFS\n"
                        + "4 - UCS\n"
                        + "5 - HC\u001B[0m\n"
        );

        System.out.print("Enter your choice: ");

        if (scanner.hasNextInt()) {
            int action = scanner.nextInt();
            scanner.nextLine();

            switch (action) {
                case 1:
                    userPlay.start();
                    break;

                case 2:
                    bfs.start();
                    break;

                case 3:
                    dfs.start();
                    break;
                case 4:
                    ucs.start();
                    break;
                case 5:
                    hc.start();
                    break;

                default:
                    showErrorMessage("Option not available yet. Please choose again:");
                    break;
            }
        } else {
            showErrorMessage("Invalid input. Please enter a valid number.");
            scanner.nextLine();

        }
    }
}
