package app.presentation.auth;

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
import app.databinding.ActivitySignUpBinding;
import app.di.AppContainer;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private SignUpViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppContainer appContainer = ((TicTacToeApplication) getApplication()).getAppContainer();
        ExecutorService executorService = appContainer.getExecutorService();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new SignUpViewModel(
                        appContainer.getSignUpUseCase(),
                        executorService
                );
            }
        }).get(SignUpViewModel.class);

        setupViews();
        observeViewModel();
    }

    private void setupViews() {
        binding.showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.passwordEditText.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );
                binding.repeatPasswordEditText.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );
            } else {
                binding.passwordEditText.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );
                binding.repeatPasswordEditText.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );
            }

            binding.passwordEditText.setSelection(binding.passwordEditText.getText().length());
            binding.repeatPasswordEditText.setSelection(binding.repeatPasswordEditText.getText().length());
        });

        binding.signUpButton.setOnClickListener(view -> {
            String login = binding.loginEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString();
            String repeatedPassword = binding.repeatPasswordEditText.getText().toString();

            viewModel.signUp(login, password, repeatedPassword);
        });

        binding.backToSignInButton.setOnClickListener(view -> finish());
    }

    private void observeViewModel() {
        viewModel.getStateLiveData().observe(this, state -> {
            if (state == null) {
                return;
            }

            boolean loading = state.isLoading();

            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.signUpButton.setEnabled(!loading);
            binding.backToSignInButton.setEnabled(!loading);
            binding.loginEditText.setEnabled(!loading);
            binding.passwordEditText.setEnabled(!loading);
            binding.repeatPasswordEditText.setEnabled(!loading);
            binding.showPasswordCheckBox.setEnabled(!loading);

            if (state.getErrorMessage() != null && !state.getErrorMessage().isBlank()) {
                Toast.makeText(this, state.getErrorMessage(), Toast.LENGTH_SHORT).show();
                viewModel.clearError();
            }

            if (state.isSuccess()) {
                Toast.makeText(this, "Регистрация выполнена успешно", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
