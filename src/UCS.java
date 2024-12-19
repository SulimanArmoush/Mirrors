import java.util.*;

public class UCS {

    PriorityQueue<GameBoard> queue = new PriorityQueue<>(Comparator.comparingInt(gameBoard -> gameBoard.cost));
    HashSet<GameBoard> visited = new HashSet<>();

    private Game game = new Game();
    GameBoard initialState;

    public UCS(GameBoard initialState) {
        this.initialState = initialState;
    }

    public void start() {
        int generate = 1;

        queue.add(initialState);
        visited.add(initialState);

        System.out.println();
        initialState.printBoard();
        System.out.println();

        while (!queue.isEmpty()) {
            GameBoard currentState = queue.poll();
            visited.add(currentState);

            if (currentState.isGoalState()) {
                game.showSuccessMessage("Solution found!");

                currentState.printBoard();
                System.out.println();
                System.out.println("\033[35mGenerated nodes: \033[0m" + generate);
                System.out.println("\033[35mVisited nodes: \033[0m" + visited.size());
                System.out.println("\033[35mNumber of solution path nodes: \033[0m" + currentState.cost);
                System.out.println();

                game.showInstruction("Do you want to print the solution path?");
                System.out.println("\033[33m0 - No");
                System.out.println("1 - Yes\033[0m");
                game.showInstruction("Enter your choice: ");

                Scanner scanner = new Scanner(System.in);
                int action = scanner.nextInt();

                if (action == 1) {
                    currentState.printPath();
                }
                return;
            }

            for (GameBoard nextState : currentState.getNextStates()) {
                if (!visited.contains(nextState) && !queue.contains(nextState)) {
                    queue.add(nextState);
                    generate++;
                }
            }
        }

        game.showErrorMessage("No solution found.");
    }
}
