import org.json.JSONObject;
import org.json.JSONArray;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Loader {


    public static class BoardConfig {
        private int rows;
        private int cols;
        private int heuristic;
        private String sourceDestination;
        private int sourceRow;
        private int sourceCol;
        private String[][] board;
        private String[][] RotateDestinations;

        public BoardConfig(int rows, int cols, int heuristic, String sourceDestination, int sourceRow, int sourceCol, String[][] board, String[][] rotateDestinations) {
            this.rows = rows;
            this.cols = cols;
            this.heuristic = heuristic;
            this.sourceDestination = sourceDestination;
            this.sourceRow = sourceRow;
            this.sourceCol = sourceCol;
            this.board = board;
            this.RotateDestinations = rotateDestinations;
        }

        public int getRows() {
            return rows;
        }

        public int getCols() {
            return cols;
        }

        public int getHeuristic() { return heuristic; }

        public int getSourceRow() {
            return sourceRow;
        }

        public int getSourceCol() {
            return sourceCol;
        }

        public String getSourceDestination() { return sourceDestination; }

        public String[][] getBoard() {
            return board;
        }

        public String[][] getRotateDestinations() {
            return RotateDestinations;
        }
    }

    public static BoardConfig loadBoardConfig(String jsonFilePath, String levelKey) throws Exception {
        String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        return parseBoardConfig(jsonContent, levelKey);
    }

    public static BoardConfig parseBoardConfig(String jsonContent, String levelKey) {
        JSONObject levelsConfig = new JSONObject(jsonContent);
        JSONObject boardConfig = levelsConfig.getJSONObject("levels").getJSONObject(levelKey);

        int rows = boardConfig.getInt("rows");
        int cols = boardConfig.getInt("cols");
        int heuristic = boardConfig.getInt("heuristic");
        int sourceRow = boardConfig.getInt("sourceRow");
        int sourceCol = boardConfig.getInt("sourceCol");
        String sourceDestination = boardConfig.getString("sourceDestination");
        JSONArray board = boardConfig.getJSONArray("board");
        JSONArray rotateDestinations = boardConfig.getJSONArray("rotateDestinations");

        String[][] boardArray = new String[rows][cols];
        String[][] destinationsArray = new String[rows][cols];

        for (int i = 0; i < rows; i++) {
            JSONArray rowBoard = board.getJSONArray(i);
            JSONArray rowDest = rotateDestinations.getJSONArray(i);
            for (int j = 0; j < cols; j++) {
                boardArray[i][j] = rowBoard.getString(j);
                destinationsArray[i][j] = rowDest.getString(j);
            }
        }

        return new BoardConfig(rows, cols,heuristic, sourceDestination, sourceRow, sourceCol, boardArray, destinationsArray);
    }
}
