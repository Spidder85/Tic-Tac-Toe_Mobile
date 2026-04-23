package app.presentation.current;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;

import app.domain.model.CurrentGame;
import app.domain.model.GameField;
import app.domain.model.UnauthorizedException;
import app.domain.model.User;
import app.domain.usecase.GetCurrentUserUseCase;
import app.domain.usecase.GetGameUseCase;
import app.domain.usecase.MakeMoveUseCase;

public class CurrentGameViewModel extends ViewModel {
    private final GetGameUseCase getGameUseCase;
    private final MakeMoveUseCase makeMoveUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final ExecutorService executorService;
    private final CurrentGameViewDataMapper mapper;

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
            ExecutorService executorService,
            CurrentGameViewDataMapper mapper
    ) {
        this.getGameUseCase = getGameUseCase;
        this.makeMoveUseCase = makeMoveUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.executorService = executorService;
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

        executorService.execute(() -> {
            try {
                User user = getCurrentUserUseCase.execute();
                if (user != null) {
                    currentUserId = user.getId();
                }

                CurrentGame loadedGame = getGameUseCase.execute(gameId);
                currentGame = loadedGame;

                gameLiveData.postValue(mapper.fromDomain(loadedGame, currentUserId));
                stateLiveData.postValue(new CurrentGameStateViewData(false, null, false));
            } catch (UnauthorizedException exception) {
                stateLiveData.postValue(
                        new CurrentGameStateViewData(false, exception.getMessage(), true)
                );
            } catch (Exception exception) {
                stateLiveData.postValue(
                        new CurrentGameStateViewData(false, exception.getMessage(), false)
                );
            }
        });
    }

    public void refreshGame(String gameId) {
        executorService.execute(() -> {
            try {
                CurrentGame loadedGame = getGameUseCase.execute(gameId);
                currentGame = loadedGame;

                gameLiveData.postValue(mapper.fromDomain(loadedGame, currentUserId));
            } catch (UnauthorizedException exception) {
                stateLiveData.postValue(
                        new CurrentGameStateViewData(false, exception.getMessage(), true)
                );
            } catch (Exception exception) {
                stateLiveData.postValue(
                        new CurrentGameStateViewData(false, exception.getMessage(), false)
                );
            }
        });
    }

    public void makeMove(int row, int col) {
        if (currentGame == null) {
            return;
        }

        CurrentGame requestGame = buildMoveRequest(currentGame, row, col);

        stateLiveData.setValue(new CurrentGameStateViewData(true, null, false));

        executorService.execute(() -> {
            try {
                CurrentGame updatedGame = makeMoveUseCase.execute(requestGame);
                currentGame = updatedGame;

                gameLiveData.postValue(mapper.fromDomain(updatedGame, currentUserId));
                stateLiveData.postValue(new CurrentGameStateViewData(false, null, false));
            } catch (UnauthorizedException exception) {
                stateLiveData.postValue(
                        new CurrentGameStateViewData(false, exception.getMessage(), true)
                );
            } catch (Exception exception) {
                stateLiveData.postValue(
                        new CurrentGameStateViewData(false, exception.getMessage(), false)
                );
            }
        });
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

        updatedCells[row][col] = resolveCurrentPlayerSymbol()
    }

    private int[][] copyCells(int[][] source) {
        int[][] result = new int[source.length][source[0].length];

        for (int row = 0; row < source.length; row++) {
            for (int col = 0; col < source[row].length; col++) {
                result[row][col] = source[row][col];
            }
        }

        return result;
    }

    private int resolveCurrentPlayerSymbol() {
        if (currentGame != null
                && currentUserId != null
                && currentUserId.equals(currentGame.getFirstPlayerId())) {
            return 1;
        }
        return 2;
    }
}
