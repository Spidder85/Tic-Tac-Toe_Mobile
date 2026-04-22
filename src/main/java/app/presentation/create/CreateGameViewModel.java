package app.presentation.create;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;

import app.domain.model.CurrentGame;
import app.domain.model.UnauthorizedException;
import app.domain.usecase.CreateGameUseCase;

public class CreateGameViewModel extends ViewModel {
    private final CreateGameUseCase createGameUseCase;
    private final ExecutorService executorService;

    private final MutableLiveData<CreateGameStateViewData> stateLiveData =
            new MutableLiveData<>(new CreateGameStateViewData(false, null, null, false));

    public CreateGameViewModel(
            CreateGameUseCase createGameUseCase,
            ExecutorService executorService
    ) {
        this.createGameUseCase = createGameUseCase;
        this.executorService = executorService;
    }

    public LiveData<CreateGameStateViewData> getStateLiveData() {
        return stateLiveData;
    }

    public void createGame(boolean computerOpponent) {
        stateLiveData.setValue(new CreateGameStateViewData(true, null, null, false));

        executorService.execute(() -> {
            try {
                CurrentGame game = createGameUseCase.execute(computerOpponent);

                stateLiveData.postValue(
                        new CreateGameStateViewData(false, null, game.getId(), false)
                );
            } catch (UnauthorizedException exception) {
                stateLiveData.postValue(
                        new CreateGameStateViewData(false, exception.getMessage(), null, true)
                );
            } catch (Exception exception) {
                stateLiveData.postValue(
                        new CreateGameStateViewData(false, exception.getMessage(), null, false)
                );
            }
        });
    }

    public void clearError() {
        CreateGameStateViewData current = stateLiveData.getValue();

        if (current == null) {
            stateLiveData.setValue(new CreateGameStateViewData(false, null, null, false));
            return;
        }

        stateLiveData.setValue(
                new CreateGameStateViewData(
                        current.isLoading(),
                        null,
                        current.getCreatedGameId(),
                        current.isUnauthorized()
                )
        );
    }

    public void clearNavigation() {
        CreateGameStateViewData current = stateLiveData.getValue();

        if (current == null) {
            stateLiveData.setValue(new CreateGameStateViewData(false, null, null, false));
            return;
        }

        stateLiveData.setValue(
                new CreateGameStateViewData(
                        current.isLoading(),
                        current.getErrorMessage(),
                        null,
                        current.isUnauthorized()
                )
        );
    }
}
