package app.data.remote;

public class CurrentGameDto {
    private String id;
    private GameFieldDto gameField;
    private String firstPlayerId;
    private String secondPlayerId;
    private String currentTurnPlayerId;
    private String winnerPlayerId;
    private String status;
    private boolean computerOpponent;

    public CurrentGameDto(){}

    public CurrentGameDto(
            String id,
            GameFieldDto gameField,
            String firstPlayerId,
            String secondPlayerId,
            String currentTurnPlayerId,
            String winnerPlayerId,
            String status,
            boolean computerOpponent
    ) {
        this.id = id;
        this.gameField = gameField;
        this.firstPlayerId = firstPlayerId;
        this.secondPlayerId = secondPlayerId;
        this.currentTurnPlayerId = currentTurnPlayerId;
        this.winnerPlayerId = winnerPlayerId;
        this.status = status;
        this.computerOpponent = computerOpponent;
    }

    public String getId() {
        return id;
    }

    public GameFieldDto getGameField() {
        return gameField;
    }

    public String getFirstPlayerId() {
        return firstPlayerId;
    }

    public String getSecondPlayerId() {
        return secondPlayerId;
    }

    public String getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }

    public String getWinnerPlayerId() {
        return winnerPlayerId;
    }

    public String getStatus() {
        return status;
    }

    public boolean isComputerOpponent() {
        return computerOpponent;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGameField(GameFieldDto gameField) {
        this.gameField = gameField;
    }

    public void setFirstPlayerId(String firstPlayerId) {
        this.firstPlayerId = firstPlayerId;
    }

    public void setSecondPlayerId(String secondPlayerId) {
        this.secondPlayerId = secondPlayerId;
    }

    public void setCurrentTurnPlayerId(String currentTurnPlayerId) {
        this.currentTurnPlayerId = currentTurnPlayerId;
    }

    public void setWinnerPlayerId(String winnerPlayerId) {
        this.winnerPlayerId = winnerPlayerId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setComputerOpponent(boolean computerOpponent) {
        this.computerOpponent = computerOpponent;
    }
}
