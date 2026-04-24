package app.di;

import com.google.gson.Gson;

import javax.inject.Singleton;

import app.data.mapper.GameMapper;
import app.data.mapper.UserMapper;
import app.presentation.auth.UserViewDataMapper;
import app.presentation.current.CurrentGameViewDataMapper;
import app.presentation.games.GameListItemViewDataMapper;
import dagger.Module;
import dagger.Provides;

@Module
public class MapperModule {
    @Provides
    @Singleton
    UserMapper provideUserMapper() {
        return new UserMapper();
    }

    @Provides
    @Singleton
    GameMapper provideGameMapper(Gson gson) {
        return new GameMapper(gson);
    }

    @Provides
    @Singleton
    UserViewDataMapper provideUserViewDataMapper() {
        return new UserViewDataMapper();
    }

    @Provides
    @Singleton
    GameListItemViewDataMapper provideGameListItemViewDataMapper() {
        return new GameListItemViewDataMapper();
    }

    @Provides
    @Singleton
    CurrentGameViewDataMapper provideCurrentGameViewDataMapper() {
        return new CurrentGameViewDataMapper();
    }
}
