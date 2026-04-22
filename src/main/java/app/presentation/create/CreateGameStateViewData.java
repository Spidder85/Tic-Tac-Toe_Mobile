package app.presentation.create;

public class CreateGameStateViewData {
    private final boolean loading;
    private final String errorMessage;
    private final String createdGameId;
    private final boolean unauthorized;

    public CreateGameStateViewData(
            boolean loading,
            String errorMessage,
            String createdGameId,
            boolean unauthorized
    ) {
        this.loading = loading;
        this.errorMessage = errorMessage;
        this.createdGameId = createdGameId;
        this.unauthorized = unauthorized;
    }

    public boolean isLoading() {
        return loading;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getCreatedGameId() {
        return createdGameId;
    }

    public boolean isUnauthorized() {
        return unauthorized;
    }
}
