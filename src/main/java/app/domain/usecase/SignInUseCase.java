package app.domain.usecase;

import app.domain.model.User;
import app.domain.repository.AuthRepository;

public class SignInUseCase {
    private final AuthRepository authRepository;

    public SignInUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public User execute(String login, String password) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Введите логин");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Введите пароль");
        }

        return authRepository.signIn(login, password);
    }
}
