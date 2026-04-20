package app.domain.model;

public class GameField {
    private final int[][] cells;

    public GameField(int[][] cells) {
        this.cells = cells;
    }

    public int[][] getCells() {
        return cells;
    }
}
