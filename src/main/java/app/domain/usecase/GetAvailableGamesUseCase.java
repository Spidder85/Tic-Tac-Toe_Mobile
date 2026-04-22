package app.domain.usecase;

import java.util.List;

import app.domain.model.CurrentGame;
import app.domain.repository.GameRepository;

public class GetAvailableGamesUseCase {
    private final GameRepository gameRepository;

    public GetAvailableGamesUseCase(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<CurrentGame> execute() {
        return gameRepository.getAvailableGames();
    }
}
