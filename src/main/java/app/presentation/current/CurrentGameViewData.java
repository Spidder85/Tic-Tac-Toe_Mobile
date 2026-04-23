package app.presentation.current;

public class CurrentGameViewData {
    private final String id;
    private final String firstPlayerLogin;
    private final String firstPlayerSymbol;
    private final String secondPlayerLogin;
    private final String secondPlayerSymbol;
    private final String statusText;
    private final boolean moveAllowed;
    private final String[][] cells;

    public CurrentGameViewData(
            String id,
            String firstPlayerLogin,
            String firstPlayerSymbol,
            String secondPlayerLogin,
            String secondPlayerSymbol,
            String statusText,
            boolean moveAllowed,
            String[][] cells
    ) {
        this.id = id;
        this.firstPlayerLogin = firstPlayerLogin;
        this.firstPlayerSymbol = firstPlayerSymbol;
        this.secondPlayerLogin = secondPlayerLogin;
        this.secondPlayerSymbol = secondPlayerSymbol;
        this.statusText = statusText;
        this.moveAllowed = moveAllowed;
        this.cells = cells;
    }

    public String getId() {
        return id;
    }

    public String getFirstPlayerLogin() {
        return firstPlayerLogin;
    }

    public String getFirstPlayerSymbol() {
        return firstPlayerSymbol;
    }

    public String getSecondPlayerLogin() {
        return secondPlayerLogin;
    }

    public String getSecondPlayerSymbol() {
        return secondPlayerSymbol;
    }

    public String getStatusText() {
        return statusText;
    }

    public boolean isMoveAllowed() {
        return moveAllowed;
    }

    public String[][] getCells() {
        return cells;
    }
}
