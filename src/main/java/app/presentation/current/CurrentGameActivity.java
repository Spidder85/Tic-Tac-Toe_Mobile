package app.presentation.current;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.ExecutorService;

import app.TicTacToeApplication;
import app.databinding.ActivityCurrentGameBinding;
import app.di.AppContainer;
import app.presentation.auth.SignInActivity;

public class CurrentGameActivity extends AppCompatActivity {
    public static final String EXTRA_GAME_ID = "extra_game_id";

    private ActivityCurrentGameBinding binding;
    private CurrentGameViewModel viewModel;
    private String gameId;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            if (gameId != null && !gameId.isBlank()) {
                viewModel.refreshGame(gameId);
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCurrentGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gameId = getIntent().getStringExtra(EXTRA_GAME_ID);
        if (gameId == null || gameId.isBlank()) {
            finish();
            return;
        }

        AppContainer appContainer = ((TicTacToeApplication) getApplication()).getAppContainer();
        ExecutorService executorService = appContainer.getExecutorService();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new CurrentGameViewModel(
                        appContainer.getGetGameUseCase(),
                        appContainer.getMakeMoveUseCase(),
                        appContainer.getGetCurrentUserUseCase(),
                        executorService,
                        new CurrentGameViewDataMapper()
                );
            }
        }).get(CurrentGameViewModel.class);

        setupViews();
        observeViewModel();
        viewModel.loadGame(gameId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(pollRunnable, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(pollRunnable);
    }

    private void setupViews() {
        binding.backButton.setOnClickListener(view -> finish());

        binding.cell00.setOnClickListener(view -> onCellClick(0, 0));
        binding.cell01.setOnClickListener(view -> onCellClick(0, 1));
        binding.cell02.setOnClickListener(view -> onCellClick(0, 2));
        binding.cell10.setOnClickListener(view -> onCellClick(1, 0));
        binding.cell11.setOnClickListener(view -> onCellClick(1, 1));
        binding.cell12.setOnClickListener(view -> onCellClick(1, 2));
        binding.cell20.setOnClickListener(view -> onCellClick(2, 0));
        binding.cell21.setOnClickListener(view -> onCellClick(2, 1));
        binding.cell22.setOnClickListener(view -> onCellClick(2, 2));
    }

    private void onCellClick(int row, int col) {
        CurrentGameViewData game = viewModel.getGameLiveData().getValue();
        if (game == null || !game.isMoveAllowed()) {
            return;
        }

        viewModel.makeMove(row, col);
    }

    private void observeViewModel() {
        viewModel.getGameLiveData().observe(this, game -> {
            if (game == null) {
                return;
            }

            binding.gameIdValueTextView.setText(game.getId());
            binding.firstPlayerValueTextView.setText(
                    game.getFirstPlayerLogin() + " (" + game.getFirstPlayerSymbol() + ")"
            );
            binding.secondPlayerValueTextView.setText(
                    game.getSecondPlayerLogin() + " (" + game.getSecondPlayerSymbol() + ")"
            );
            binding.statusValueTextView.setText(game.getStatusText());

            renderCells(game);
            setBoardEnabled(game.isMoveAllowed());
        });

        viewModel.getStateLiveData().observe(this, state -> {
            if (state == null) {
                return;
            }

            binding.progressBar.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);

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
            }
        });
    }


    private void renderCells(CurrentGameViewData game) {
        String[][] cells = game.getCells();
        
        binding.cell00.setText(cells[0][0]);
        binding.cell01.setText(cells[0][1]);
        binding.cell02.setText(cells[0][2]);
        binding.cell10.setText(cells[1][0]);
        binding.cell11.setText(cells[1][1]);
        binding.cell12.setText(cells[1][2]);
        binding.cell20.setText(cells[2][0]);
        binding.cell21.setText(cells[2][1]);
        binding.cell22.setText(cells[2][2]);
    }

    private void setBoardEnabled(boolean enabled) {
        binding.cell00.setEnabled(enabled);
        binding.cell01.setEnabled(enabled);
        binding.cell02.setEnabled(enabled);
        binding.cell10.setEnabled(enabled);
        binding.cell11.setEnabled(enabled);
        binding.cell12.setEnabled(enabled);
        binding.cell20.setEnabled(enabled);
        binding.cell21.setEnabled(enabled);
        binding.cell22.setEnabled(enabled);

    }
}
