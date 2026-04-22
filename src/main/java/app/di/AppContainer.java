package app.di;

import android.content.Context;

import androidx.room.Room;

import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.data.local.AppDatabase;
import app.data.mapper.GameMapper;
import app.data.mapper.UserMapper;
import app.data.remote.ApiConfig;
import app.data.remote.AuthHeaderFactory;
import app.data.remote.TicTacToeApi;
import app.data.repository.AuthRepositoryImpl;
import app.data.repository.GameRepositoryImpl;
import app.domain.repository.AuthRepository;
import app.domain.repository.GameRepository;
import app.domain.usecase.CreateGameUseCase;
import app.domain.usecase.GetAvailableGamesUseCase;
import app.domain.usecase.GetCurrentUserUseCase;
import app.domain.usecase.GetGameUseCase;
import app.domain.usecase.JoinGameUseCase;
import app.domain.usecase.LogoutUseCase;
import app.domain.usecase.MakeMoveUseCase;
import app.domain.usecase.SignInUseCase;
import app.domain.usecase.SignUpUseCase;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppContainer {
    private final ExecutorService executorService;

    private final AppDatabase database;
    private final TicTacToeApi api;

    private final AuthRepository authRepository;
    private final GameRepository gameRepository;

    private final SignUpUseCase signUpUseCase;
    private final SignInUseCase signInUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final LogoutUseCase logoutUseCase;

    private final CreateGameUseCase createGameUseCase;
    private final GetAvailableGamesUseCase getAvailableGamesUseCase;
    private final JoinGameUseCase joinGameUseCase;
    private final GetGameUseCase getGameUseCase;
    private final MakeMoveUseCase makeMoveUseCase;

    public AppContainer(Context context) {
        executorService = Executors.newFixedThreadPool(4);

        Gson gson = new Gson();

        database = Room.databaseBuilder(
                context,
                AppDatabase.class,
                "tic_tac_toe.db"
        ).build();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        api = retrofit.create(TicTacToeApi.class);

        AuthHeaderFactory authHeaderFactory = new AuthHeaderFactory();
        UserMapper userMapper = new UserMapper();
        GameMapper gameMapper = new GameMapper(gson);

        authRepository = new AuthRepositoryImpl(
                api,
                database.userDao(),
                database.gameDao(),
                userMapper,
                authHeaderFactory
        );

        gameRepository = new GameRepositoryImpl(
                api,
                database.userDao(),
                database.gameDao(),
                gameMapper,
                authHeaderFactory
        );

        signUpUseCase = new SignUpUseCase(authRepository);
        signInUseCase = new SignInUseCase(authRepository);
        getCurrentUserUseCase = new GetCurrentUserUseCase(authRepository);
        logoutUseCase = new LogoutUseCase(authRepository);

        createGameUseCase = new CreateGameUseCase(gameRepository);
        getAvailableGamesUseCase = new GetAvailableGamesUseCase(gameRepository);
        joinGameUseCase = new JoinGameUseCase(gameRepository);
        getGameUseCase = new GetGameUseCase(gameRepository);
        makeMoveUseCase = new MakeMoveUseCase(gameRepository);
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public SignUpUseCase getSignUpUseCase() {
        return signUpUseCase;
    }

    public SignInUseCase getSignInUseCase() {
        return signInUseCase;
    }

    public GetCurrentUserUseCase getGetCurrentUserUseCase() {
        return getCurrentUserUseCase;
    }

    public LogoutUseCase getLogoutUseCase() {
        return logoutUseCase;
    }

    public CreateGameUseCase getCreateGameUseCase() {
        return createGameUseCase;
    }

    public GetAvailableGamesUseCase getGetAvailableGamesUseCase() {
        return getAvailableGamesUseCase;
    }

    public JoinGameUseCase getJoinGameUseCase() {
        return joinGameUseCase;
    }

    public GetGameUseCase getGetGameUseCase() {
        return getGameUseCase;
    }

    public MakeMoveUseCase getMakeMoveUseCase() {
        return makeMoveUseCase;
    }
}
