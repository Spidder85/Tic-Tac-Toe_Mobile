package app.domain.repository;

import java.util.List;

import app.domain.model.CurrentGame;

public interface GameRepository {
    CurrentGame createGame(boolean computerOpponent);

    List<CurrentGame> getAvailableGames();

    CurrentGame joinGame(String gameId);

    CurrentGame getGame(String gameId);

    CurrentGame makeMove(CurrentGame game);
}
