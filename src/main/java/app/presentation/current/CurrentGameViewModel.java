package app.presentation.current;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import app.domain.model.CurrentGame;
import app.domain.model.GameField;
import app.domain.model.UnauthorizedException;
import app.domain.model.User;
import app.domain.usecase.GetCurrentUserUseCase;
import app.domain.usecase.GetGameUseCase;
import app.domain.usecase.MakeMoveUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CurrentGameViewModel extends ViewModel {
    private final GetGameUseCase getGameUseCase;
    private final MakeMoveUseCase makeMoveUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final CurrentGameViewDataMapper mapper;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<CurrentGameStateViewData> stateLiveData =
            new MutableLiveData<>(new CurrentGameStateViewData(false, null, false));

    private final MutableLiveData<CurrentGameViewData> gameLiveData =
            new MutableLiveData<>();

    private String currentUserId;
    private CurrentGame currentGame;

    public CurrentGameViewModel(
            GetGameUseCase getGameUseCase,
            MakeMoveUseCase makeMoveUseCase,
            GetCurrentUserUseCase getCurrentUserUseCase,
            CurrentGameViewDataMapper mapper
    ) {
        this.getGameUseCase = getGameUseCase;
        this.makeMoveUseCase = makeMoveUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.mapper = mapper;
    }

    public LiveData<CurrentGameStateViewData> getStateLiveData() {
        return stateLiveData;
    }

    public LiveData<CurrentGameViewData> getGameLiveData() {
        return gameLiveData;
    }

    public void loadGame(String gameId) {
        stateLiveData.setValue(new CurrentGameStateViewData(true, null, false));

        disposables.add(
                Single.fromCallable(() -> {
                    User user = getCurrentUserUseCase.execute();
                    String resolvedUserId = user != null ? user.getId() : null;
                    CurrentGame loadedGame = getGameUseCase.execute(gameId);
                    return new LoadedGameData(resolvedUserId, loadedGame);
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(data -> {
                            currentUserId = data.currentUserId;
                            currentGame = data.game;
                            gameLiveData.setValue(mapper.fromDomain(data.game, currentUserId));
                            stateLiveData.setValue(new CurrentGameStateViewData(false, null, false));
                        }, this::handleError)
        );
    }

    public void refreshGame(String gameId) {
        disposables.add(
                Single.fromCallable(() -> getGameUseCase.execute(gameId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(loadedGame -> {
                            currentGame = loadedGame;
                            gameLiveData.setValue(mapper.fromDomain(loadedGame, currentUserId));
                        }, this::handleError)
        );
    }

    public void makeMove(int row, int col) {
        if (currentGame == null) {
            return;
        }

        CurrentGame requestGame = buildMoveRequest(currentGame, row, col);

        stateLiveData.setValue(new CurrentGameStateViewData(true, null, false));

        disposables.add(
                Single.fromCallable(() -> makeMoveUseCase.execute(requestGame))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(updatedGame -> {
                            currentGame = updatedGame;
                            gameLiveData.setValue(mapper.fromDomain(updatedGame, currentUserId));
                            stateLiveData.setValue(new CurrentGameStateViewData(false, null, false));
                        }, this::handleError)
        );
    }

    public void clearError() {
        CurrentGameStateViewData current = stateLiveData.getValue();

        if (current == null) {
            stateLiveData.setValue(new CurrentGameStateViewData(false, null, false));
            return;
        }

        stateLiveData.setValue(
                new CurrentGameStateViewData(
                        current.isLoading(),
                        null,
                        current.isUnauthorized()
                )
        );
    }

    private CurrentGame buildMoveRequest(CurrentGame game, int row, int col) {
        int[][] source = game.getGameField().getCells();
        int[][] updatedCells = new int[source.length][source[0].length];

        for (int i = 0; i < source.length; i++) {
            System.arraycopy(source[i], 0, updatedCells[i], 0, source[0].length);
        }

        updatedCells[row][col] = resolveCurrentUserMark(game);

        return new CurrentGame(
                game.getId(),
                new GameField(updatedCells),
                game.getFirstPlayerId(),
                game.getFirstPlayerLogin(),
                game.getSecondPlayerId(),
                game.getSecondPlayerLogin(),
                game.getCurrentTurnPlayerId(),
                game.getWinnerPlayerId(),
                game.getStatus(),
                game.isComputerOpponent()
        );
    }

    private int resolveCurrentUserMark(CurrentGame game) {
        if (currentUserId != null && currentUserId.equals(game.getFirstPlayerId())) {
            return 1;
        }
        return 2;
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof UnauthorizedException) {
            stateLiveData.setValue(
                    new CurrentGameStateViewData(false, throwable.getMessage(), true)
            );
            return;
        }

        stateLiveData.setValue(
                new CurrentGameStateViewData(false, throwable.getMessage(), false)
        );
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    private static final class LoadedGameData {
        private final String currentUserId;
        private final CurrentGame game;

        private LoadedGameData(String currentUserId, CurrentGame game) {
            this.currentUserId = currentUserId;
            this.game = game;
        }
    }
}
