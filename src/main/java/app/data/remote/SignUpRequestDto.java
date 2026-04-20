package app.data.remote;

public class SignUpRequestDto {
    private String login;
    private String password;

    public SignUpRequestDto(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
