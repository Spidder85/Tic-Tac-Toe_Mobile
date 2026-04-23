package app.presentation.current;

public class CurrentGameStateViewData {
    private final boolean loading;
    private final String errorMessage;
    private final boolean unauthorized;

    public CurrentGameStateViewData(
            boolean loading,
            String errorMessage,
            boolean unauthorized
    ) {
        this.loading = loading;
        this.errorMessage = errorMessage;
        this.unauthorized = unauthorized;
    }

    public boolean isLoading() {
        return loading;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isUnauthorized() {
        return unauthorized;
    }
}
