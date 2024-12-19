import java.util.Scanner;

public class UserPlay {
    private GameBoard gameBoard;
    private final Scanner scanner;

    private Game game = new Game();

    public UserPlay(GameBoard gameBoard) {
        this.gameBoard = new GameBoard(gameBoard);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        clearConsole();
        gameBoard.printBoard();

        game.showInstruction(
                "R - Rotating Mirror\n" +
                        "F - Fixed Mirror\n" +
                        "T - Sliding Mirror\n" +
                        "# - Obstacle\n" +
                        "* (Red) - Goal\n" +
                        "\n" +
                        "Enter Mirror Cell (like: 3 2):\n");

        while (true) {
            if (gameBoard.isGoalState()) {
                game.showSuccessMessage("Congratulations! You've solved the puzzle.");
                break;
            }

            System.out.print("Cell: ");
            String input = scanner.nextLine();
            int rows, cols;

            if (tryParseInput(input)) {
                rows = Integer.parseInt(input.split(" ")[0]);
                cols = Integer.parseInt(input.split(" ")[1]);

                Action(rows, cols);
                System.out.println();
                gameBoard.printBoard();
            } else {
                game.showErrorMessage("Invalid input. Please try again.");
            }
        }
    }

    private void Action(int r, int c) {

        if (gameBoard.getType(r, c) == CellType.Source) {
            game.showInstruction("this is a Light Source\n" + "we well rotate it");
            handleSourceLight(r, c);
        } else if (gameBoard.getType(r, c) == CellType.RotatingMirror) {
            game.showInstruction("this is a Rotation Mirror\n" + "we well rotate it");
            gameBoard.makeRotate(r, c);
        } else if (gameBoard.getType(r, c) == CellType.TransitionalMirror) {
            game.showInstruction("this is a Transitional Mirror");
            handleTransitionAction(r, c);
        } else {
            game.showErrorMessage("Invalid input. Please try again.");
        }
    }


    private void handleTransitionAction(int rows, int cols) {
        game.showInstruction("Which destination?");
        System.out.println("\u001B[33m0 - UP\n1 - DOWN\n2 - RIGHT\n3 - LEFT\u001B[0m"); // Yellow text
        System.out.print("Enter your choice: ");

        if (scanner.hasNextInt()) {
            int directionChoice = scanner.nextInt();
            scanner.nextLine();

            TransitionDestination destination = TransitionDestination.values()[directionChoice];
            gameBoard.makeTransition(rows, cols, destination);

        } else {
            game.showErrorMessage("Invalid direction choice.");
        }
    }


    private void handleSourceLight(int rows, int cols) {
        game.showInstruction("Which destination?");
        System.out.println("\u001B[33m0 - UP\n1 - DOWN\n2 - RIGHT\n3 - LEFT\n4 - UP_RIGHT\n5 - UP_LEFT\n6 - DOWN_RIGHT\n7 - DOWN_LEFT  \u001B[0m"); // Yellow text
        System.out.print("Enter your choice: ");

        if (scanner.hasNextInt()) {
            int directionChoice = scanner.nextInt();
            scanner.nextLine();

            LightDestination destination = LightDestination.values()[directionChoice];
            gameBoard.SourceDestination(rows, cols, destination);

        } else {
            game.showErrorMessage("Invalid direction choice.");
        }
    }


    private boolean tryParseInput(String input) {
        String[] parts = input.split(" ");
        if (parts.length == 2) {
            try {
                Integer.parseInt(parts[0]);
                Integer.parseInt(parts[1]);
                return true;
            } catch (NumberFormatException ignored) {
            }
        }
        return false;
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
