package app.domain.usecase;

import app.domain.model.CurrentGame;
import app.domain.repository.GameRepository;

public class CreateGameUseCase {
    private final GameRepository gameRepository;

    public CreateGameUseCase(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public CurrentGame execute(boolean computerOpponent) {
        return gameRepository.createGame(computerOpponent);
    }
}
