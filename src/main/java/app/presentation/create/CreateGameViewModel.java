package app.presentation.create;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import app.domain.model.UnauthorizedException;
import app.domain.usecase.CreateGameUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CreateGameViewModel extends ViewModel {
    private final CreateGameUseCase createGameUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<CreateGameStateViewData> stateLiveData =
            new MutableLiveData<>(new CreateGameStateViewData(false, null, null, false));

    public CreateGameViewModel(CreateGameUseCase createGameUseCase) {
        this.createGameUseCase = createGameUseCase;
    }

    public LiveData<CreateGameStateViewData> getStateLiveData() {
        return stateLiveData;
    }

    public void createGame(boolean computerOpponent) {
        stateLiveData.setValue(new CreateGameStateViewData(true, null, null, false));

        disposables.add(
                Single.fromCallable(() -> createGameUseCase.execute(computerOpponent))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(game -> stateLiveData.setValue(
                                new CreateGameStateViewData(false, null, game.getId(), false)
                        ), throwable -> {
                            if (throwable instanceof UnauthorizedException) {
                                stateLiveData.setValue(
                                        new CreateGameStateViewData(false, throwable.getMessage(), null, true)
                                );
                                return;
                            }

                            stateLiveData.setValue(
                                    new CreateGameStateViewData(false, throwable.getMessage(), null, false)
                            );
                        })
        );
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

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
