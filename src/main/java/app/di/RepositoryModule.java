package app.di;

import javax.inject.Singleton;

import app.data.local.GameDao;
import app.data.local.UserDao;
import app.data.mapper.GameMapper;
import app.data.mapper.UserMapper;
import app.data.remote.AuthHeaderFactory;
import app.data.remote.TicTacToeApi;
import app.data.repository.AuthRepositoryImpl;
import app.data.repository.GameRepositoryImpl;
import app.domain.repository.AuthRepository;
import app.domain.repository.GameRepository;
import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {
    @Provides
    @Singleton
    AuthRepository provideAuthRepository(
            TicTacToeApi api,
            UserDao userDao,
            GameDao gameDao,
            UserMapper userMapper,
            AuthHeaderFactory authHeaderFactory
    ) {
        return new AuthRepositoryImpl(
                api,
                userDao,
                gameDao,
                userMapper,
                authHeaderFactory
        );
    }

    @Provides
    @Singleton
    GameRepository provideGameRepository(
            TicTacToeApi api,
            UserDao userDao,
            GameDao gameDao,
            GameMapper gameMapper,
            AuthHeaderFactory authHeaderFactory
    ) {
        return new GameRepositoryImpl(
                api,
                userDao,
                gameDao,
                gameMapper,
                authHeaderFactory
        );
    }
}
