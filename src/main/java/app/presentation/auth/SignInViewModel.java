package app.presentation.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import app.domain.usecase.SignInUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SignInViewModel extends ViewModel {
    private final SignInUseCase signInUseCase;
    private final UserViewDataMapper userViewDataMapper;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<AuthStateViewData> stateLiveData =
            new MutableLiveData<>(new AuthStateViewData(false, null, false));

    private final MutableLiveData<UserViewData> currentUserLiveData =
            new MutableLiveData<>(null);

    public SignInViewModel(
            SignInUseCase signInUseCase,
            UserViewDataMapper userViewDataMapper
    ) {
        this.signInUseCase = signInUseCase;
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

        disposables.add(
                Single.fromCallable(() -> signInUseCase.execute(login, password))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(user -> {
                            currentUserLiveData.setValue(userViewDataMapper.fromDomain(user));
                            stateLiveData.setValue(new AuthStateViewData(false, null, true));
                        }, throwable -> {
                            stateLiveData.setValue(
                                    new AuthStateViewData(false, throwable.getMessage(), false)
                            );
                        })
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
