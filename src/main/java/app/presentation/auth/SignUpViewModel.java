package app.presentation.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;

import app.domain.usecase.SignUpUseCase;

public class SignUpViewModel extends ViewModel {
    private final SignUpUseCase signUpUseCase;
    private final ExecutorService executorService;

    private final MutableLiveData<AuthStateViewData> stateLiveData =
            new MutableLiveData<>(new AuthStateViewData(false, null, false));

    public SignUpViewModel(SignUpUseCase signUpUseCase, ExecutorService executorService) {
        this.signUpUseCase = signUpUseCase;
        this.executorService = executorService;
    }

    public LiveData<AuthStateViewData> getStateLiveData() {
        return stateLiveData;
    }

    public void signUp(String login, String password, String repeatedPassword) {
        if (repeatedPassword == null || !repeatedPassword.equals(password)) {
            stateLiveData.setValue(new AuthStateViewData(false, "Пароли не совпадают", false));
            return;
        }

        stateLiveData.setValue(new AuthStateViewData(true, null, false));

        executorService.execute(() -> {
            try {
                boolean result = signUpUseCase.execute(login, password);

                if (result) {
                    stateLiveData.postValue(new AuthStateViewData(false, null, true));
                } else {
                    stateLiveData.postValue(
                            new AuthStateViewData(false, "Не удалось зарегистрироваться", false)
                    );
                }
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
