package app.domain.model;

public class CurrentGame {
    private final String id;
    private final GameField gameField;

    private final String firstPlayerId;
    private final String firstPlayerLogin;

    private final String secondPlayerId;
    private final String secondPlayerLogin;

    private final String currentTurnPlayerId;
    private final String winnerPlayerId;

    private final GameStatus status;
    private final boolean computerOpponent;

    public CurrentGame(
            String id,
            GameField gameField,
            String firstPlayerId,
            String firstPlayerLogin,
            String secondPlayerId,
            String secondPlayerLogin,
            String currentTurnPlayerId,
            String winnerPlayerId,
            GameStatus status,
            boolean computerOpponent
    ) {
        this.id = id;
        this.gameField = gameField;
        this.firstPlayerId = firstPlayerId;
        this.firstPlayerLogin = firstPlayerLogin;
        this.secondPlayerId = secondPlayerId;
        this.secondPlayerLogin = secondPlayerLogin;
        this.currentTurnPlayerId = currentTurnPlayerId;
        this.winnerPlayerId = winnerPlayerId;
        this.status = status;
        this.computerOpponent = computerOpponent;
    }

    public String getId() {
        return id;
    }

    public GameField getGameField() {
        return gameField;
    }

    public String getFirstPlayerId() {
        return firstPlayerId;
    }

    public String getFirstPlayerLogin() {
        return firstPlayerLogin;
    }

    public String getSecondPlayerId() {
        return secondPlayerId;
    }

    public String getSecondPlayerLogin() {
        return secondPlayerLogin;
    }

    public String getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }

    public String getWinnerPlayerId() {
        return winnerPlayerId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public boolean isComputerOpponent() {
        return computerOpponent;
    }
}
