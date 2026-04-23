package app.domain.usecase;

import app.domain.model.CurrentGame;
import app.domain.repository.GameRepository;

public class MakeMoveUseCase {
    private final GameRepository gameRepository;

    public MakeMoveUseCase(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public CurrentGame execute(CurrentGame game) {
        if (game == null) {
            throw new IllegalArgumentException("Игра не загружена");
        }

        return gameRepository.makeMove(game);
    }
}
