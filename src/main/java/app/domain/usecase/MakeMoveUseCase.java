package app.domain.usecase;

import app.domain.model.CurrentGame;
import app.domain.model.GameField;
import app.domain.repository.GameRepository;

public class MakeMoveUseCase {
    private final GameRepository gameRepository;

    public MakeMoveUseCase(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public CurrentGame execute(CurrentGame game, int row, int column, String currentUserId) {
        if (game == null) {
            throw new IllegalArgumentException("Игра не загружена");
        }

        if (currentUserId == null || currentUserId.isBlank()) {
            throw new IllegalArgumentException("Пользователь не авторизован");
        }

        if (row < 0 || row > 2 || column < 0 || column > 2) {
            throw new IllegalArgumentException("Не корректная клетка");
        }

        int[][] sourceCells = game.getGameField().getCells();
        int[][] updatedCells = new int[sourceCells.length][sourceCells[0].length];

        for (int i = 0; i < sourceCells.length; i++) {
            System.arraycopy(sourceCells[i], 0, updatedCells[i], 0, sourceCells[i].length);
        }

        if (updatedCells[row][column] != 0) {
            throw new IllegalArgumentException("Клетка уже занята");
        }

        int playerMark = currentUserId.equals(game.getFirstPlayerId()) ? 1 : 2;
        updatedCells[row][column] = playerMark;

        CurrentGame updatedGame = new CurrentGame(
                game.getId(),
                new GameField(updatedCells),
                game.getFirstPlayerId(),
                game.getFirstPlayerLogin(),
                game.getSecondPlayerId(),
                game.getSecondPlayerLogin(),
                game.getCurrentTurnPlayerId(),
                game.getWinnerPlayerId(),
                game.getStatus(),
                game.isComputerOpponent()
        );

        return gameRepository.makeMove(updatedGame);
    }
}
