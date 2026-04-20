package app.domain.repository;

import app.domain.model.User;

public interface AuthRepository {
    boolean signUp(String login, String password);

    User signIn(String login, String password);

    User getCurrentUser();

    void logout();
}
