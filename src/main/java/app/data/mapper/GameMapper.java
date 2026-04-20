package app.data.mapper;

import com.google.gson.Gson;

import app.data.local.GameEntity;
import app.data.remote.CurrentGameDto;
import app.data.remote.GameFieldDto;
import app.domain.model.CurrentGame;
import app.domain.model.GameField;
import app.domain.model.GameStatus;

public class GameMapper {
    private final Gson gson;

    public GameMapper(Gson gson) {
        this.gson = gson;
    }

    public CurrentGame fromDto(
            CurrentGameDto dto,
            String firstPlayerLogin,
            String secondPlayerLogin
    ) {
        return new CurrentGame(
                dto.getId(),
                new GameField(dto.getGameField().getCells()),
                dto.getFirstPlayerId(),
                firstPlayerLogin,
                dto.getSecondPlayerId(),
                secondPlayerLogin,
                dto.getCurrentTurnPlayerId(),
                dto.getWinnerPlayerId(),
                GameStatus.valueOf(dto.getStatus()),
                dto.isComputerOpponent()
        );
    }

    public CurrentGameDto toDto(CurrentGame game) {
        return new CurrentGameDto(
                game.getId(),
                new GameFieldDto(game.getGameField().getCells()),
                game.getFirstPlayerId(),
                game.getSecondPlayerId(),
                game.getCurrentTurnPlayerId(),
                game.getWinnerPlayerId(),
                game.getStatus().name(),
                game.isComputerOpponent()
        );
    }

    public CurrentGame fromEntity(GameEntity entity) {
        if (entity== null) {
            return null;
        }

        int[][] cells = gson.fromJson(entity.cellsJson, int[][].class);

        return new CurrentGame(
                entity.id,
                new GameField(cells),
                entity.firstPlayerId,
                entity.firstPlayerLogin,
                entity.secondPlayerId,
                entity.secondPlayerLogin,
                entity.currentTurnPlayerId,
                entity.winnerPlayerId,
                GameStatus.valueOf(entity.status),
                entity.computerOpponent
        );
    }

    public GameEntity toEntity(CurrentGame game) {
        String cellsJson = gson.toJson(game.getGameField().getCells());

        return new GameEntity(
                game.getId(),
                cellsJson,
                game.getFirstPlayerId(),
                game.getFirstPlayerLogin(),
                game.getSecondPlayerId(),
                game.getSecondPlayerLogin(),
                game.getCurrentTurnPlayerId(),
                game.getWinnerPlayerId(),
                game.getStatus().name(),
                game.isComputerOpponent()
        );
    }
}
