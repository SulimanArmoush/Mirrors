import java.util.*;

enum LightDestination {
    UP, DOWN, RIGHT, LEFT, UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT, NONE
}

enum TransitionDestination {
    UP, DOWN, RIGHT, LEFT
}

enum RotateDestinations {
    RotateClockwise, RotateCounterClockwise, NONE
}


enum CellType {
    Obstacle, Target, Empty, Source, Path, FixedMirror, RotatingMirror, TransitionalMirror
}

public class GameBoard {

    private CellType[][] board;
    private RotateDestinations[][] rotateDestinations;
    private int rows, cols;
    public int cost ,heuristic;
    public final int finalHeuristic;
    private GameBoard parent;
    private int sourceRow, sourceCol;

    private LightDestination sourceDestination;

    public CellType getType(int r, int c) {
        return board[r][c];
    }

    private static final LightDestination[] RotateClockwise = {LightDestination.UP, LightDestination.UP_RIGHT, LightDestination.RIGHT, LightDestination.DOWN_RIGHT, LightDestination.DOWN, LightDestination.DOWN_LEFT, LightDestination.LEFT, LightDestination.UP_LEFT};

    private static final LightDestination[] RotateCounterClockwise = {LightDestination.UP, LightDestination.UP_LEFT, LightDestination.LEFT, LightDestination.DOWN_LEFT, LightDestination.DOWN, LightDestination.DOWN_RIGHT, LightDestination.RIGHT, LightDestination.UP_RIGHT};


    public GameBoard(String levelKey, String jsonFilePath) {

        Loader.BoardConfig config = null;
        try {
            config = Loader.loadBoardConfig(jsonFilePath, levelKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.cost = 0;
        this.finalHeuristic = config.getHeuristic();
        this.heuristic = this.finalHeuristic;
        this.rows = config.getRows();
        this.cols = config.getCols();
        this.sourceRow = config.getSourceRow();
        this.sourceCol = config.getSourceCol();
        this.sourceDestination = LightDestination.valueOf(config.getSourceDestination());

        this.rotateDestinations = new RotateDestinations[rows][cols];
        this.board = new CellType[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.rotateDestinations[i][j] = RotateDestinations.valueOf(config.getRotateDestinations()[i][j]);
                this.board[i][j] = CellType.valueOf(config.getBoard()[i][j]);
            }
        }

        lightPath(sourceDestination, sourceRow, sourceCol);
    }


    public GameBoard(GameBoard other) {

        this.rows = other.rows;
        this.cols = other.cols;
        this.cost = other.cost;
        this.finalHeuristic = other.finalHeuristic;
        this.heuristic = other.heuristic;

        this.sourceCol = other.sourceCol;
        this.sourceRow = other.sourceRow;
        this.sourceDestination = other.sourceDestination;

        this.rotateDestinations = new RotateDestinations[rows][cols];
        this.board = new CellType[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.rotateDestinations[i][j] = other.rotateDestinations[i][j];
                this.board[i][j] = other.board[i][j];
            }
        }
        lightPath(sourceDestination, sourceRow, sourceCol);
    }


    public boolean isMirror(CellType cellType) {
        return cellType == CellType.FixedMirror || cellType == CellType.RotatingMirror || cellType == CellType.TransitionalMirror;
    }


    public void printBoard() {
        System.out.print("     ");
        for (int j = 0; j < cols; j++) {
            System.out.print(j + " ");
        }
        System.out.println();
        System.out.println("   ╔" + "═".repeat(cols * 2 + 1) + "╗");
        for (int i = 0; i < rows; i++) {
            System.out.printf("%2d ║ ", i);
            for (int j = 0; j < cols; j++) {
                System.out.print(getCellRepresentation(board[i][j]));
            }
            System.out.println("║"); // نهاية الصف
        }
        System.out.println("   ╚" + "═".repeat(cols * 2 + 1) + "╝");
    }

    private String getCellRepresentation(CellType cellType) {
        return switch (cellType) {
            case Empty -> "  ";
            case Source -> "\033[33m* \033[0m";
            case Target -> "\033[31m* \033[0m";
            case Obstacle -> "\033[90m# \033[0m";
            case Path -> "\033[32m* \033[0m";
            case FixedMirror -> "\033[36mF \033[0m";
            case TransitionalMirror -> "\033[36mT \033[0m";
            case RotatingMirror -> "\033[36mR \033[0m";
        };
    }

    public boolean isGoalState() {
        clearPaths();
        this.heuristic = this.finalHeuristic;
        return lightPath(sourceDestination, sourceRow, sourceCol);
    }

    private void clearPaths() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == CellType.Path) {
                    board[i][j] = CellType.Empty;
                }
            }
        }
    }

    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public boolean canRotate(int row, int col) {
        return board[row][col] == CellType.RotatingMirror;
    }


    public boolean canTransition(int row, int col, TransitionDestination destination) {
        if (board[row][col] != CellType.TransitionalMirror) {
            return false;
        }

        int[][] directions = {
                {-1, 0},  // UP
                {1, 0},   // DOWN
                {0, 1},   // RIGHT
                {0, -1}   // LEFT
        };

        int dirIndex = destination.ordinal();
        int newRow = row + directions[dirIndex][0];
        int newCol = col + directions[dirIndex][1];

        return isInBounds(newRow, newCol) && (board[newRow][newCol] == CellType.Empty || board[newRow][newCol] == CellType.Path);
    }


    public void makeTransition(int row, int col, TransitionDestination destination) {
        if (!canTransition(row, col, destination)) return;

        int[][] directions = {
                {-1, 0},  // UP
                {1, 0},   // DOWN
                {0, 1},   // RIGHT
                {0, -1}   // LEFT
        };

        int dirIndex = destination.ordinal();
        int newRow = row + directions[dirIndex][0];
        int newCol = col + directions[dirIndex][1];

        board[newRow][newCol] = CellType.TransitionalMirror;
        rotateDestinations[newRow][newCol] = rotateDestinations[row][col];

        board[row][col] = CellType.Empty;
        rotateDestinations[row][col] = RotateDestinations.NONE;

        clearPaths();
        this.heuristic = this.finalHeuristic;
        lightPath(sourceDestination, sourceRow, sourceCol);
    }


    public void makeRotate(int row, int col) {
        if (!isInBounds(row, col) || !canRotate(row, col)) return;

        rotateDestinations[row][col] = rotateDestinations[row][col] == RotateDestinations.RotateClockwise ? RotateDestinations.RotateCounterClockwise : RotateDestinations.RotateClockwise;

        clearPaths();
        this.heuristic = this.finalHeuristic;
        lightPath(sourceDestination, sourceRow, sourceCol);
    }

    public void SourceDestination(int row, int col, LightDestination lightDestination) {
        if (board[row][col] == CellType.Source) sourceDestination = lightDestination;

        clearPaths();
        this.heuristic = this.finalHeuristic;
        lightPath(sourceDestination, sourceRow, sourceCol);
    }


    public boolean lightPath(LightDestination destination, int r, int c) {
        int[][] directions = {
                {-1, 0},  // UP
                {1, 0},   // DOWN
                {0, 1},   // RIGHT
                {0, -1},  // LEFT
                {-1, 1},  // UP_RIGHT
                {-1, -1}, // UP_LEFT
                {1, 1},   // DOWN_RIGHT
                {1, -1}   // DOWN_LEFT
        };

        int dirIndex = destination.ordinal();
        if (dirIndex < 0 || dirIndex >= directions.length) return false;

        int dr = directions[dirIndex][0];
        int dc = directions[dirIndex][1];

        int currentRow = r + dr;
        int currentCol = c + dc;

        while (isInBounds(currentRow, currentCol)) {
            if (board[currentRow][currentCol] == CellType.Empty) {
                board[currentRow][currentCol] = CellType.Path;
            } else if (board[currentRow][currentCol] == CellType.Target) {
                heuristic--;
                return true;
            } else if (isMirror(board[currentRow][currentCol])) {
                heuristic--;
                LightDestination newDirection = getNewLightDirection(rotateDestinations[currentRow][currentCol], destination);
                return lightPath(newDirection, currentRow, currentCol);
            } else {
                break;
            }

            currentRow += dr;
            currentCol += dc;
        }

        return false;
    }

    public LightDestination getNewLightDirection(RotateDestinations mirrorType, LightDestination currentDirection) {
        LightDestination[] rotationArray;
        if (mirrorType == RotateDestinations.RotateClockwise) {
            rotationArray = RotateClockwise;
        } else if (mirrorType == RotateDestinations.RotateCounterClockwise) {
            rotationArray = RotateCounterClockwise;
        } else {
            return LightDestination.NONE;
        }

        int currentIndex = Arrays.asList(rotationArray).indexOf(currentDirection);

        if (currentIndex == -1) {
            return LightDestination.NONE;
        }

        int newIndex = (currentIndex + 2) % rotationArray.length;

        return rotationArray[newIndex];
    }


    public List<GameBoard> getNextStates() {
        List<GameBoard> possibleMoves = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                CellType cell = board[i][j];

                if (cell == CellType.RotatingMirror) {
                    GameBoard clockwiseBoard = new GameBoard(this);
                    clockwiseBoard.cost++;
                    clockwiseBoard.parent = this;
                    clockwiseBoard.makeRotate(i, j);
                    possibleMoves.add(clockwiseBoard);

                    GameBoard counterClockwiseBoard = new GameBoard(this);
                    counterClockwiseBoard.cost++;
                    counterClockwiseBoard.parent = this;
                    counterClockwiseBoard.makeRotate(i, j);
                    counterClockwiseBoard.makeRotate(i, j);
                    possibleMoves.add(counterClockwiseBoard);
                }

                if (cell == CellType.TransitionalMirror) {
                    for (TransitionDestination dest : TransitionDestination.values()) {
                        if (canTransition(i, j, dest)) {
                            GameBoard newBoard = new GameBoard(this);
                            newBoard.cost++;
                            newBoard.parent = this;
                            newBoard.makeTransition(i, j, dest);
                            possibleMoves.add(newBoard);
                        }
                    }
                }

                if (cell == CellType.Source) {
                    for (LightDestination lightDest : LightDestination.values()) {
                        if (lightDest != LightDestination.NONE) {
                            GameBoard newBoard = new GameBoard(this);
                            newBoard.cost++;
                            newBoard.parent = this;
                            newBoard.SourceDestination(i, j, lightDest);
                            possibleMoves.add(newBoard);
                        }
                    }
                }
            }
        }

        return possibleMoves;
    }


    public void printPath() {
        GameBoard current = this;
        Stack<GameBoard> path = new Stack<GameBoard>();

        while (current != null) {
            path.push(current);
            current = current.parent;
        }

        System.out.println();
        path.pop().printBoard();
        System.out.println();

        int i = 1;
        while (!path.isEmpty()) {
            current = path.pop();

            System.out.println();
            System.out.println("step : " + i++);
            current.printBoard();
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GameBoard other = (GameBoard) obj;
        return Arrays.deepEquals(this.board, other.board) &&
                Arrays.deepEquals(this.rotateDestinations, other.rotateDestinations) &&
                this.sourceRow == other.sourceRow &&
                this.sourceCol == other.sourceCol &&
                this.sourceDestination == other.sourceDestination;
    }

    @Override
    public int hashCode() {
        int result = Arrays.deepHashCode(board);
        result = 31 * result + Arrays.deepHashCode(rotateDestinations);
        result = 31 * result + sourceRow;
        result = 31 * result + sourceCol;
        result = 31 * result + sourceDestination.hashCode();
        return result;
    }
}
