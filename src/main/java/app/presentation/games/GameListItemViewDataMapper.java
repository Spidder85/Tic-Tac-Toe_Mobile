package app.presentation.games;

import app.domain.model.CurrentGame;

public class GameListItemViewDataMapper {
    public GameListItemViewData fromDomain(CurrentGame game) {
        if (game == null) {
            return null;
        }

        String creatorLogin = game.getFirstPlayerLogin();

        if (creatorLogin == null || creatorLogin.isBlank()) {
            creatorLogin = "Неизвестный игрок";
        }

        return new GameListItemViewData(
                game.getId(),
                creatorLogin
        );
    }
}
