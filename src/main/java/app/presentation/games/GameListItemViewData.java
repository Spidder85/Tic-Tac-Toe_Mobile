package app.presentation.games;

public class GameListItemViewData {
    private final String id;
    private final String creatorLogin;

    public GameListItemViewData(String id, String creatorLogin) {
        this.id = id;
        this.creatorLogin = creatorLogin;
    }

    public String getId() {
        return id;
    }

    public String getCreatorLogin() {
        return creatorLogin;
    }
}
