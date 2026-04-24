package app.di;

import javax.inject.Singleton;

import app.presentation.auth.SignInActivity;
import app.presentation.auth.SignUpActivity;
import app.presentation.create.CreateGameActivity;
import app.presentation.current.CurrentGameActivity;
import app.presentation.games.GamesActivity;
import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class,
        DataModule.class,
        MapperModule.class,
        RepositoryModule.class,
        UseCaseModule.class
})
public interface AppComponent {
    void inject(SignInActivity activity);
    void inject(SignUpActivity activity);
    void inject(GamesActivity activity);
    void inject(CreateGameActivity activity);
    void inject(CurrentGameActivity activity);
}
