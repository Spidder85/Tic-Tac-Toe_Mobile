package app.presentation.games;

public class GamesStateViewData {
    private final boolean loading;
    private final String errorMessage;
    private final boolean loggedOut;
    private final boolean unauthorized;
    private final boolean openCreateGameScreen;
    private final String openedGameId;

    public GamesStateViewData(
            boolean loading,
            String errorMessage,
            boolean loggedOut,
            boolean unauthorized,
            boolean openCreateGameScreen,
            String openedGameId
    ) {
        this.loading = loading;
        this.errorMessage = errorMessage;
        this.loggedOut = loggedOut;
        this.unauthorized = unauthorized;
        this.openCreateGameScreen = openCreateGameScreen;
        this.openedGameId = openedGameId;
    }

    public boolean isLoading() {
        return loading;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isLoggedOut() {
        return loggedOut;
    }

    public boolean isUnauthorized() {
        return unauthorized;
    }

    public boolean isOpenCreateGameScreen() {
        return openCreateGameScreen;
    }

    public String getOpenedGameId() {
        return openedGameId;
    }
}
