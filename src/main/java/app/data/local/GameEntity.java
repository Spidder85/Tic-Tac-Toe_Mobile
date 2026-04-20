package app.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "games")
public class GameEntity {
    @PrimaryKey
    @NonNull
    public String id;

    public String cellsJson;

    public String firstPlayerId;
    public String firstPlayerLogin;

    public String secondPlayerId;
    public String secondPlayerLogin;

    public String currentTurnPlayerId;
    public String winnerPlayerId;

    public String status;
    public boolean computerOpponent;

    public GameEntity(
            @NonNull String id,
            String cellsJson,
            String firstPlayerId,
            String firstPlayerLogin,
            String secondPlayerId,
            String secondPlayerLogin,
            String currentTurnPlayerId,
            String winnerPlayerId,
            String status,
            boolean computerOpponent
    ) {
        this.id = id;
        this.cellsJson = cellsJson;
        this.firstPlayerId = firstPlayerId;
        this.firstPlayerLogin = firstPlayerLogin;
        this.secondPlayerId = secondPlayerId;
        this.secondPlayerLogin = secondPlayerLogin;
        this.currentTurnPlayerId = currentTurnPlayerId;
        this.winnerPlayerId = winnerPlayerId;
        this.status = status;
        this.computerOpponent = computerOpponent;
    }
}
