package app.data.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.data.local.GameDao;
import app.data.local.UserDao;
import app.data.local.UserEntity;
import app.data.mapper.GameMapper;
import app.data.remote.AuthHeaderFactory;
import app.data.remote.CreateGameRequestDto;
import app.data.remote.CurrentGameDto;
import app.data.remote.TicTacToeApi;
import app.data.remote.UserDto;
import app.domain.model.CurrentGame;
import app.domain.model.UnauthorizedException;
import app.domain.repository.GameRepository;

import retrofit2.Response;

public class GameRepositoryImpl implements GameRepository {
    private final TicTacToeApi api;
    private final UserDao userDao;
    private final GameDao gameDao;
    private final GameMapper gameMapper;
    private final AuthHeaderFactory authHeaderFactory;

    public GameRepositoryImpl(
            TicTacToeApi api,
            UserDao userDao,
            GameDao gameDao,
            GameMapper gameMapper,
            AuthHeaderFactory authHeaderFactory
    ) {
        this.api = api;
        this.userDao = userDao;
        this.gameDao = gameDao;
        this.gameMapper = gameMapper;
        this.authHeaderFactory = authHeaderFactory;
    }

    @Override
    public CurrentGame createGame(boolean computerOpponent) {
        try {
            String authorization = authorization();

            Response<CurrentGameDto> response = api.createGame(
                    authorization,
                    new CreateGameRequestDto(computerOpponent)
            ).execute();

            CurrentGame game = handleGameResponse(authorization, response, "Ошибка создания игры");
            gameDao.saveGame((gameMapper.toEntity(game)));

            return game;
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка при создании игры", exception);
        }
    }

    @Override
    public List<CurrentGame> getAvailableGames() {
        try {
            String authorization = authorization();

            Response<List<CurrentGameDto>> response = api.getAvailableGames(authorization).execute();

            if (!response.isSuccessful()) {
                throw createException(response.code(), "Ошибка получения списка игр");
            }

            List<CurrentGameDto> dtoList = response.body();
            List<CurrentGame> games = new ArrayList<>();

            if (dtoList != null) {
                for (CurrentGameDto dto : dtoList) {
                    games.add(mapWithUsers(authorization, dto));
                }
            }

            gameDao.clear();

            for (CurrentGame game : games) {
                gameDao.saveGame(gameMapper.toEntity(game));
            }

            return games;
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка сети при получении списка игр", exception);
        }
    }

    @Override
    public CurrentGame joinGame(String gameId) {
        try {
            String authorization = authorization();

            Response<CurrentGameDto> response = api.joinGame(
                    authorization,
                    gameId
            ).execute();

            CurrentGame game = handleGameResponse(authorization, response, "Ошибка подключения к игре");
            gameDao.saveGame(gameMapper.toEntity(game));

            return game;
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка сети при подключении к игре", exception);
        }
    }

    @Override
    public CurrentGame getGame(String gameId) {
        try {
            String authorization = authorization();

            Response<CurrentGameDto> response = api.getGame(
                    authorization,
                    gameId
            ).execute();

            CurrentGame game = handleGameResponse(authorization, response, "Ошибка получения игры");
            gameDao.saveGame(gameMapper.toEntity(game));

            return game;
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка сети при получении игры", exception);
        }
    }

    @Override
    public CurrentGame makeMove(CurrentGame game) {
        try {
            String authorization = authorization();

            Response<CurrentGameDto> response = api.makeMove(
                    authorization,
                    game.getId(),
                    gameMapper.toDto(game)
            ).execute();

            CurrentGame updatedGame = handleGameResponse(authorization, response, "Ошибка выполнения хода");
            gameDao.saveGame(gameMapper.toEntity(updatedGame));

            return updatedGame;
        } catch (Exception exception) {
            throw new RuntimeException("Ошибка сети при выполнении хода", exception);
        }
    }

    private CurrentGame handleGameResponse(
            String authorization,
            Response<CurrentGameDto> response,
            String errorMessage
    ) {
        if (!response.isSuccessful()) {
            throw createException(response.code(), errorMessage);
        }

        CurrentGameDto dto = response.body();

        if (dto == null) {
            throw new RuntimeException("Сервер не вернул игру");
        }

        return mapWithUsers(authorization, dto);
    }

    private CurrentGame mapWithUsers(String authorization, CurrentGameDto dto) {
        String firstLogin = getUserLogin(authorization, dto.getFirstPlayerId());
        String secondLogin = getUserLogin(authorization, dto.getSecondPlayerId());

        return gameMapper.fromDto(dto, firstLogin, secondLogin);
    }

    private String getUserLogin(String authorization, String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }

        try {
            Response<UserDto> response = api.getUser(authorization, userId).execute();

            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }

            return response.body().getLogin();
        } catch (IOException exception) {
            return null;
        }
    }

    private String authorization() {
        UserEntity user = userDao.getCurrentUser();

        if (user == null) {
            throw new UnauthorizedException();
        }

        return authHeaderFactory.create(user.login, user.password);
    }

    private RuntimeException createException(int code, String defaultMessage) {
        if (code == 401) {
            return new UnauthorizedException();
        }

        return new RuntimeException(defaultMessage + ". Код ответа: " + code);
    }
}
