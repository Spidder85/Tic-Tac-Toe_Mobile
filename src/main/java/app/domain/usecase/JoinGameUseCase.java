package app.domain.usecase;

import app.domain.model.CurrentGame;
import app.domain.repository.GameRepository;

public class JoinGameUseCase {
    private final GameRepository gameRepository;

    public JoinGameUseCase(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public CurrentGame execute(String gameId) {
        if (gameId == null || gameId.isBlank()) {
            throw new IllegalArgumentException("Не указан идентификатор игры");
        }

        return gameRepository.joinGame(gameId);
    }
}
