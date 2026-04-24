package app.presentation.create;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import app.TicTacToeApplication;
import app.databinding.ActivityCreateGameBinding;
import app.domain.usecase.CreateGameUseCase;
import app.presentation.auth.SignInActivity;
import app.presentation.current.CurrentGameActivity;

public class CreateGameActivity extends AppCompatActivity {
    private ActivityCreateGameBinding binding;
    private CreateGameViewModel viewModel;

    @Inject
    CreateGameUseCase createGameUseCase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ((TicTacToeApplication) getApplication()).getAppComponent().inject(this);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new CreateGameViewModel(createGameUseCase);
            }
        }).get(CreateGameViewModel.class);

        setupViews();
        observeViewModel();
    }

    private void setupViews() {
        binding.createWithComputerButton.setOnClickListener(
                view -> viewModel.createGame(true)
        );

        binding.createWithPlayerButton.setOnClickListener(
                view -> viewModel.createGame(false)
        );

        binding.backButton.setOnClickListener(view -> finish());
    }

    private void observeViewModel() {
        viewModel.getStateLiveData().observe(this, state -> {
            if (state == null) {
                return;
            }

            boolean loading = state.isLoading();

            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.createWithComputerButton.setEnabled(!loading);
            binding.createWithPlayerButton.setEnabled(!loading);
            binding.backButton.setEnabled(!loading);

            if (state.getErrorMessage() != null && !state.getErrorMessage().isBlank()) {
                Toast.makeText(this, state.getErrorMessage(), Toast.LENGTH_SHORT).show();
                viewModel.clearError();
                return;
            }

            if (state.isUnauthorized()) {
                Intent intent = new Intent(this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return;
            }

            if (state.getCreatedGameId() != null && !state.getCreatedGameId().isBlank()) {
                String gameId = state.getCreatedGameId();
                viewModel.clearNavigation();

                Intent intent = new Intent(this, CurrentGameActivity.class);
                intent.putExtra(CurrentGameActivity.EXTRA_GAME_ID, gameId);
                startActivity(intent);
                finish();
            }
        });
    }
}
