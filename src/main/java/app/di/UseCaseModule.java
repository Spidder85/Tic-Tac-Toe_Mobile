package app.di;

import javax.inject.Singleton;

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
import dagger.Module;
import dagger.Provides;

@Module
public class UseCaseModule {
    @Provides
    @Singleton
    SignUpUseCase provideSignUpUseCase(AuthRepository authRepository) {
        return new SignUpUseCase(authRepository);
    }

    @Provides
    @Singleton
    SignInUseCase provideSignInUseCase(AuthRepository authRepository) {
        return new SignInUseCase(authRepository);
    }

    @Provides
    @Singleton
    GetCurrentUserUseCase provideGetCurrentUserUseCase(AuthRepository authRepository) {
        return new GetCurrentUserUseCase(authRepository);
    }

    @Provides
    @Singleton
    LogoutUseCase provideLogoutUseCase(AuthRepository authRepository) {
        return new LogoutUseCase(authRepository);
    }

    @Provides
    @Singleton
    CreateGameUseCase provideCreateGameUseCase(GameRepository gameRepository) {
        return new CreateGameUseCase(gameRepository);
    }

    @Provides
    @Singleton
    GetAvailableGamesUseCase provideGetAvailableGamesUseCase(GameRepository gameRepository) {
        return new GetAvailableGamesUseCase(gameRepository);
    }

    @Provides
    @Singleton
    JoinGameUseCase provideJoinGameUseCase(GameRepository gameRepository) {
        return new JoinGameUseCase(gameRepository);
    }

    @Provides
    @Singleton
    GetGameUseCase provideGetGameUseCase(GameRepository gameRepository) {
        return new GetGameUseCase(gameRepository);
    }

    @Provides
    @Singleton
    MakeMoveUseCase provideMakeMoveUseCase(GameRepository gameRepository) {
        return new MakeMoveUseCase(gameRepository);
    }
}
