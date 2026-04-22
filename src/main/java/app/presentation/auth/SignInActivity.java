package app.presentation.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.ExecutorService;

import app.TicTacToeApplication;
import app.databinding.ActivitySignInBinding;
import app.di.AppContainer;
import app.presentation.games.GamesActivity;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private SignInViewModel viewModel;
    private UserViewData authorizedUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppContainer appContainer = ((TicTacToeApplication) getApplication()).getAppContainer();
        ExecutorService executorService = appContainer.getExecutorService();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new SignInViewModel(
                        appContainer.getSignInUseCase(),
                        executorService,
                        new UserViewDataMapper()
                );
            }
        }).get(SignInViewModel.class);

        setupViews();
        observeViewModel();
    }

    private void setupViews() {
        binding.showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.passwordEditText.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );
            } else {
                binding.passwordEditText.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );
            }

            binding.passwordEditText.setSelection(binding.passwordEditText.getText().length());
        });

        binding.signInButton.setOnClickListener(view -> {
            String login = binding.loginEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString();

            viewModel.signIn(login, password);
        });

        binding.openSignUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        viewModel.getCurrentUserLiveData().observe(this, userViewData -> {
            authorizedUser = userViewData;
        });

        viewModel.getStateLiveData().observe(this, state -> {
            if (state == null) {
                return;
            }

            boolean loading = state.isLoading();

            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.signInButton.setEnabled(!loading);
            binding.openSignUpButton.setEnabled(!loading);
            binding.loginEditText.setEnabled(!loading);
            binding.passwordEditText.setEnabled(!loading);
            binding.showPasswordCheckBox.setEnabled(!loading);

            if (state.getErrorMessage() != null && !state.getErrorMessage().isBlank()) {
                Toast.makeText(this, state.getErrorMessage(), Toast.LENGTH_SHORT).show();
                viewModel.clearError();
            }

            if (state.isSuccess() && authorizedUser != null) {
                Intent intent = new Intent(this, GamesActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
