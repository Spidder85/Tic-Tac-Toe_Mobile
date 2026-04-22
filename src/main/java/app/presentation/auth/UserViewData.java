package app.presentation.auth;

public class UserViewData {
    private final String id;
    private final String login;

    public UserViewData(String id, String login) {
        this.id = id;
        this.login = login;
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }
}
