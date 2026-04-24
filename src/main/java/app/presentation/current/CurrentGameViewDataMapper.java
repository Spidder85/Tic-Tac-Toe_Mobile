package app.presentation.current;

import androidx.annotation.NonNull;

import app.domain.model.CurrentGame;
import app.domain.model.GameStatus;

public class CurrentGameViewDataMapper {
    public CurrentGameViewData fromDomain(CurrentGame game, String currentUserId) {
        String firstPlayerLogin = game.getFirstPlayerLogin() == null || game.getFirstPlayerLogin().isBlank()
                ? "Игрок 1"
                : game.getFirstPlayerLogin();

        String secondPlayerLogin;
        if (game.isComputerOpponent()) {
            secondPlayerLogin = "Компьютер";
        } else if (game.getSecondPlayerLogin() == null || game.getSecondPlayerLogin().isBlank()) {
            secondPlayerLogin = "Ожидание соперника";
        } else {
            secondPlayerLogin = game.getSecondPlayerLogin();
        }

        return new CurrentGameViewData(
                game.getId(),
                firstPlayerLogin,
                "X",
                secondPlayerLogin,
                "O",
                mapStatusText(game, currentUserId),
                isMoveAllowed(game, currentUserId),
                mapCells(game)
        );
    }

    private boolean isMoveAllowed(CurrentGame game, String currentUserId) {
        return game.getStatus() == GameStatus.TURN
                && currentUserId != null
                && currentUserId.equals(game.getCurrentTurnPlayerId());
    }

    private String mapStatusText(CurrentGame game, String currentUserId) {
        switch (game.getStatus()) {
            case WAITING_FOR_PLAYERS:
                return "Ожидание игроков";
            case DRAW:
                return "Ничья";
            case WIN:
                return currentUserId != null && currentUserId.equals(game.getWinnerPlayerId())
                        ? "Победа"
                        : "Поражение";
            case TURN:
                if (currentUserId != null && currentUserId.equals(game.getCurrentTurnPlayerId())) {
                    return "Ваш ход";
                }

                return "Ходит соперник " + getCurrentTurnLogin(game);
            default:
                return "";
        }
    }

    @NonNull
    private String getCurrentTurnLogin(CurrentGame game) {
        String currentTurnLogin = null;

        if (game.getCurrentTurnPlayerId() != null) {
            if (game.getCurrentTurnPlayerId().equals(game.getFirstPlayerId())) {
                currentTurnLogin = game.getFirstPlayerLogin();
            } else if (game.getCurrentTurnPlayerId().equals(game.getSecondPlayerId())) {
                currentTurnLogin = game.getSecondPlayerLogin();
            }
        }

        if (currentTurnLogin == null || currentTurnLogin.isBlank()) {
            currentTurnLogin = game.isComputerOpponent()
                    ? "Компьютер"
                    : "соперник";
        }
        return currentTurnLogin;
    }

    private String[][] mapCells(CurrentGame game) {
        int[][] source = game.getGameField().getCells();
        String[][] result = new String[3][3];

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int value = source[row][col];
                if (value == 1) {
                    result[row][col] = "X";
                } else if (value == 2) {
                    result[row][col] = "O";
                } else {
                    result[row][col] = "";
                }
            }
        }

        return result;
    }
}
