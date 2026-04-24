package app.presentation.games;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import app.domain.model.CurrentGame;
import app.domain.model.UnauthorizedException;
import app.domain.usecase.GetAvailableGamesUseCase;
import app.domain.usecase.JoinGameUseCase;
import app.domain.usecase.LogoutUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GamesViewModel extends ViewModel {
    private final GetAvailableGamesUseCase getAvailableGamesUseCase;
    private final JoinGameUseCase joinGameUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GameListItemViewDataMapper mapper;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<GamesStateViewData> stateLiveData =
            new MutableLiveData<>(new GamesStateViewData(false, null, false, false, false, null));

    private final MutableLiveData<List<GameListItemViewData>> gamesLiveData =
            new MutableLiveData<>(new ArrayList<>());

    public GamesViewModel(
            GetAvailableGamesUseCase getAvailableGamesUseCase,
            JoinGameUseCase joinGameUseCase,
            LogoutUseCase logoutUseCase,
            GameListItemViewDataMapper mapper
    ) {
        this.getAvailableGamesUseCase = getAvailableGamesUseCase;
        this.joinGameUseCase = joinGameUseCase;
        this.logoutUseCase = logoutUseCase;
        this.mapper = mapper;
    }

    public LiveData<GamesStateViewData> getStateLiveData() {
        return stateLiveData;
    }

    public LiveData<List<GameListItemViewData>> getGamesLiveData() {
        return gamesLiveData;
    }

    public void loadGames() {
        stateLiveData.setValue(new GamesStateViewData(true, null, false, false, false, null));

        disposables.add(
                Single.fromCallable(getAvailableGamesUseCase::execute)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(games -> {
                            List<GameListItemViewData> items = new ArrayList<>();

                            for (CurrentGame game : games) {
                                GameListItemViewData item = mapper.fromDomain(game);
                                if (item != null) {
                                    items.add(item);
                                }
                            }

                            gamesLiveData.setValue(items);
                            stateLiveData.setValue(new GamesStateViewData(false, null, false, false, false, null));
                        }, this::handleError)
        );
    }

    public void joinGame(String gameId) {
        stateLiveData.setValue(new GamesStateViewData(true, null, false, false, false, null));

        disposables.add(
                Single.fromCallable(() -> joinGameUseCase.execute(gameId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(currentGame -> stateLiveData.setValue(new GamesStateViewData(
                                false,
                                null,
                                false,
                                false,
                                false,
                                currentGame.getId()
                        )), this::handleError)
        );
    }

    public void openCreateGameScreen() {
        GamesStateViewData current = stateLiveData.getValue();
        if (current == null) {
            stateLiveData.setValue(new GamesStateViewData(false, null, false, false, true, null));
            return;
        }

        stateLiveData.setValue(new GamesStateViewData(
                current.isLoading(),
                current.getErrorMessage(),
                current.isLoggedOut(),
                current.isUnauthorized(),
                true,
                null
        ));
    }

    public void logout() {
        stateLiveData.setValue(new GamesStateViewData(true, null, false, false, false, null));

        disposables.add(
                Completable.fromAction(logoutUseCase::execute)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> stateLiveData.setValue(
                                new GamesStateViewData(false, null, true, false, false, null)
                        ), throwable -> stateLiveData.setValue(new GamesStateViewData(
                                false,
                                throwable.getMessage(),
                                false,
                                false,
                                false,
                                null
                        )))
        );
    }

    public void clearError() {
        GamesStateViewData current = stateLiveData.getValue();
        if (current == null) {
            stateLiveData.setValue(new GamesStateViewData(false, null, false, false, false, null));
            return;
        }

        stateLiveData.setValue(new GamesStateViewData(
                current.isLoading(),
                null,
                current.isLoggedOut(),
                current.isUnauthorized(),
                current.isOpenCreateGameScreen(),
                current.getOpenedGameId()
        ));
    }

    public void clearNavigation() {
        GamesStateViewData current = stateLiveData.getValue();
        if (current == null) {
            stateLiveData.setValue(new GamesStateViewData(false, null, false, false, false, null));
            return;
        }

        stateLiveData.setValue(new GamesStateViewData(
                current.isLoading(),
                current.getErrorMessage(),
                current.isLoggedOut(),
                current.isUnauthorized(),
                false,
                null
        ));
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof UnauthorizedException) {
            stateLiveData.setValue(new GamesStateViewData(
                    false,
                    throwable.getMessage(),
                    false,
                    true,
                    false,
                    null
            ));
            return;
        }

        stateLiveData.setValue(new GamesStateViewData(
                false,
                throwable.getMessage(),
                false,
                false,
                false,
                null
        ));
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
