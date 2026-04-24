package app.presentation.games;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import app.domain.model.CurrentGame;
import app.domain.model.UnauthorizedException;
import app.domain.usecase.GetAvailableGamesUseCase;
import app.domain.usecase.JoinGameUseCase;
import app.domain.usecase.LogoutUseCase;

public class GamesViewModel extends ViewModel {
    private final GetAvailableGamesUseCase getAvailableGamesUseCase;
    private final JoinGameUseCase joinGameUseCase;
    private final LogoutUseCase logoutUseCase;
    private final ExecutorService executorService;
    private final GameListItemViewDataMapper mapper;

    private final MutableLiveData<GamesStateViewData> stateLiveData =
            new MutableLiveData<>(new GamesStateViewData(false, null, false, false, false, null));

    private final MutableLiveData<List<GameListItemViewData>> gamesLiveData =
            new MutableLiveData<>(new ArrayList<>());

    public GamesViewModel(
            GetAvailableGamesUseCase getAvailableGamesUseCase,
            JoinGameUseCase joinGameUseCase,
            LogoutUseCase logoutUseCase,
            ExecutorService executorService,
            GameListItemViewDataMapper mapper
    ) {
        this.getAvailableGamesUseCase = getAvailableGamesUseCase;
        this.joinGameUseCase = joinGameUseCase;
        this.logoutUseCase = logoutUseCase;
        this.executorService = executorService;
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

        executorService.execute(() -> {
            try {
                List<CurrentGame> games = getAvailableGamesUseCase.execute();
                List<GameListItemViewData> items = new ArrayList<>();

                for (CurrentGame game : games) {
                    GameListItemViewData item = mapper.fromDomain(game);
                    if (item != null) {
                        items.add(item);
                    }
                }

                gamesLiveData.postValue(items);
                stateLiveData.postValue(new GamesStateViewData(false, null, false, false, false, null));
            } catch (UnauthorizedException exception) {
                stateLiveData.postValue(new GamesStateViewData(
                        false,
                        exception.getMessage(),
                        false,
                        true,
                        false,
                        null
                ));
            } catch (Exception exception) {
                stateLiveData.postValue(new GamesStateViewData(
                        false,
                        exception.getMessage(),
                        false,
                        false,
                        false,
                        null
                ));
            }
        });
    }

    public void joinGame(String gameId) {
        stateLiveData.setValue(new GamesStateViewData(true, null, false, false, false, null));

        executorService.execute(() -> {
            try {
                CurrentGame currentGame = joinGameUseCase.execute(gameId);
                stateLiveData.postValue(new GamesStateViewData(
                        false,
                        null,
                        false,
                        false,
                        false,
                        currentGame.getId()
                ));
            } catch (UnauthorizedException exception) {
                stateLiveData.postValue(new GamesStateViewData(
                        false,
                        exception.getMessage(),
                        false,
                        true,
                        false,
                        null
                ));
            } catch (Exception exception) {
                stateLiveData.postValue(new GamesStateViewData(
                        false,
                        exception.getMessage(),
                        false,
                        false,
                        false,
                        null
                ));
            }
        });
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

        executorService.execute(() -> {
            try {
                logoutUseCase.execute();
                stateLiveData.postValue(new GamesStateViewData(false, null, true, false, false, null));
            } catch (Exception exception) {
                stateLiveData.postValue(new GamesStateViewData(
                        false,
                        exception.getMessage(),
                        false,
                        false,
                        false,
                        null
                ));
            }
        });
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
}
