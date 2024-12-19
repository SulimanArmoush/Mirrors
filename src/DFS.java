import java.util.*;

public class DFS {

    Stack<GameBoard> stack = new Stack<>();
    HashSet<GameBoard> visited = new HashSet<>();

    private Game game = new Game();
    GameBoard initialState;

    public DFS(GameBoard initialState) {
        this.initialState = initialState;
    }

    public void start() {
        int generate = 1;

        stack.add(initialState);
        visited.add(initialState);

        System.out.println();
        initialState.printBoard();
        System.out.println();

        while (!stack.isEmpty()) {
            GameBoard currentState = stack.pop();
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
                if (!visited.contains(nextState) && !stack.contains(nextState)) {
                    stack.add(nextState);
                    generate++;
                }
            }
        }

        game.showErrorMessage("No solution found.");
    }
}
