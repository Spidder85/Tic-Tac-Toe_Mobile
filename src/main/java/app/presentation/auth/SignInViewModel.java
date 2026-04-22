package app.presentation.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;

import app.domain.model.User;
import app.domain.usecase.SignInUseCase;

public class SignInViewModel extends ViewModel {
    private final SignInUseCase signInUseCase;
    private final ExecutorService executorService;
    private final UserViewDataMapper userViewDataMapper;

    private final MutableLiveData<AuthStateViewData> stateLiveData =
            new MutableLiveData<>(new AuthStateViewData(false, null, false));

    private final MutableLiveData<UserViewData> currentUserLiveData =
            new MutableLiveData<>(null);

    public SignInViewModel(
            SignInUseCase signInUseCase,
            ExecutorService executorService,
            UserViewDataMapper userViewDataMapper
    ) {
        this.signInUseCase = signInUseCase;
        this.executorService = executorService;
        this.userViewDataMapper = userViewDataMapper;
    }

    public LiveData<AuthStateViewData> getStateLiveData() {
        return stateLiveData;
    }

    public LiveData<UserViewData> getCurrentUserLiveData() {
        return currentUserLiveData;
    }

    public void signIn(String login, String password) {
        stateLiveData.setValue(new AuthStateViewData(true, null, false));

        executorService.execute(() -> {
            try {
                User user = signInUseCase.execute(login, password);
                currentUserLiveData.postValue(userViewDataMapper.fromDomain(user));
                stateLiveData.postValue(new AuthStateViewData(false, null, true));
            } catch (Exception exception) {
                stateLiveData.postValue(
                        new AuthStateViewData(false, exception.getMessage(), false)
                );
            }
        });
    }

    public void clearError() {
        AuthStateViewData currentState = stateLiveData.getValue();

        if (currentState == null) {
            stateLiveData.setValue(new AuthStateViewData(false, null, false));
            return;
        }

        stateLiveData.setValue(
                new AuthStateViewData(
                        currentState.isLoading(),
                        null,
                        currentState.isSuccess()
                )
        );
    }
}
