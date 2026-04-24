package app.presentation.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import app.domain.usecase.SignUpUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SignUpViewModel extends ViewModel {
    private final SignUpUseCase signUpUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<AuthStateViewData> stateLiveData =
            new MutableLiveData<>(new AuthStateViewData(false, null, false));

    public SignUpViewModel(SignUpUseCase signUpUseCase) {
        this.signUpUseCase = signUpUseCase;
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

        disposables.add(
                Single.fromCallable(() -> signUpUseCase.execute(login, password))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result) {
                                stateLiveData.setValue(new AuthStateViewData(false, null, true));
                            } else {
                                stateLiveData.setValue(
                                        new AuthStateViewData(false, "Не удалось зарегистрироваться", false)
                                );
                            }
                        }, throwable -> stateLiveData.setValue(
                                new AuthStateViewData(false, throwable.getMessage(), false)
                        ))
        );
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

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
