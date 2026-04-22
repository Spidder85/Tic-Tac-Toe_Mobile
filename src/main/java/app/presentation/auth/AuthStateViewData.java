package app.presentation.auth;

public class AuthStateViewData {
    private final boolean loading;
    private final String errorMessage;
    private final boolean success;

    public AuthStateViewData(boolean loading, String errorMessage, boolean success) {
        this.loading = loading;
        this.errorMessage = errorMessage;
        this.success = success;
    }

    public boolean isLoading() {
        return loading;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }
}
