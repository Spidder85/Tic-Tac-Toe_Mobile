package app.data.repository;

import java.io.IOException;

import app.data.local.GameDao;
import app.data.local.UserDao;
import app.data.local.UserEntity;
import app.data.mapper.UserMapper;
import app.data.remote.AuthHeaderFactory;
import app.data.remote.SignUpRequestDto;
import app.data.remote.TicTacToeApi;
import app.domain.model.UnauthorizedException;
import app.domain.model.User;
import app.domain.repository.AuthRepository;

import retrofit2.Response;

public class AuthRepositoryImpl implements AuthRepository {
    private final TicTacToeApi api;
    private final UserDao userDao;
    private final GameDao gameDao;
    private final UserMapper userMapper;
    private final AuthHeaderFactory authHeaderFactory;

    public AuthRepositoryImpl(
            TicTacToeApi api,
            UserDao userDao,
            GameDao gameDao,
            UserMapper userMapper,
            AuthHeaderFactory authHeaderFactory
    ) {
        this.api = api;
        this.userDao = userDao;
        this.gameDao = gameDao;
        this.userMapper = userMapper;
        this.authHeaderFactory = authHeaderFactory;
    }

    @Override
    public boolean signUp(String login, String password) {
        try {
            Response<Boolean> response = api.signUp(
                    new SignUpRequestDto(login, password)
            ).execute();

            if (!response.isSuccessful()) {
                throw createException(response.code(), "Ошибка регистрации");
            }

            Boolean body = response.body();
            return body != null && body;
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка сети пр регистрации", exception);
        }
    }

    @Override
    public User signIn(String login, String password) {
        try {
            String authorization = authHeaderFactory.create(login, password);

            Response<String> response = api.signIn(authorization).execute();

            if (!response.isSuccessful()) {
                if (response.code() == 401) {
                    throw new UnauthorizedException("Неверный логин или пароль");
                }

                throw createException(response.code(), "Ошибка авторизации");
            }

            String userId = response.body();

            if (userId == null || userId.isBlank()) {
                throw new RuntimeException("Сервер не вернул UUID пользователя");
            }

            userId = userId.replace("\"", "");

            UserEntity savedUser = userDao.getCurrentUser();

            if (savedUser != null && !savedUser.id.equals(userId)) {
                userDao.clear();
                gameDao.clear();
            }

            User user = new User(userId, login, password);
            userDao.saveUser(userMapper.toEntity(user));

            return user;
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка сети при авторизации", exception);
        }
    }

    @Override
    public User getCurrentUser() {
        return userMapper.fromEntity(userDao.getCurrentUser());
    }

    @Override
    public void logout() {
        userDao.clear();
        gameDao.clear();
    }

    private RuntimeException createException(int code, String defaultMessage) {
        if (code == 401 ) {
            return new UnauthorizedException();
        }

        return new RuntimeException(defaultMessage + ". Код ответа: " + code);
    }
}
