package app.domain.usecase;

import app.domain.repository.AuthRepository;

public class SignUpUseCase {
    private final AuthRepository authRepository;

    public SignUpUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public boolean execute(String login, String password) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Введите логин");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Введите пароль");
        }

        return authRepository.signUp(login, password);
    }
}
