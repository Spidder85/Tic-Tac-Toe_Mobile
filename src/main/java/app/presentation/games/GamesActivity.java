package app.presentation.games;

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
import app.databinding.ActivityGamesBinding;
import app.domain.usecase.GetAvailableGamesUseCase;
import app.domain.usecase.JoinGameUseCase;
import app.domain.usecase.LogoutUseCase;
import app.presentation.auth.SignInActivity;
import app.presentation.create.CreateGameActivity;
import app.presentation.current.CurrentGameActivity;

public class GamesActivity extends AppCompatActivity {
    private ActivityGamesBinding binding;
    private GamesViewModel viewModel;
    private GamesAdapter adapter;

    @Inject
    GetAvailableGamesUseCase getAvailableGamesUseCase;

    @Inject
    JoinGameUseCase joinGameUseCase;

    @Inject
    LogoutUseCase logoutUseCase;

    @Inject
    GameListItemViewDataMapper mapper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGamesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ((TicTacToeApplication) getApplication()).getAppComponent().inject(this);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new GamesViewModel(
                        getAvailableGamesUseCase,
                        joinGameUseCase,
                        logoutUseCase,
                        mapper
                );
            }
        }).get(GamesViewModel.class);

        setupViews();
        observeViewModel();

        viewModel.loadGames();
    }

    private void setupViews() {
        adapter = new GamesAdapter(item -> viewModel.joinGame(item.getId()));
        binding.recyclerView.setAdapter(adapter);

        binding.swipeRefreshLayout.setOnRefreshListener(() -> viewModel.loadGames());
        binding.logoutButton.setOnClickListener(view -> viewModel.logout());
        binding.createGameButton.setOnClickListener(view -> viewModel.openCreateGameScreen());
    }

    private void observeViewModel() {
        viewModel.getGamesLiveData().observe(this, items -> {
            adapter.submitList(items);
            boolean isEmpty = items == null || items.isEmpty();
            binding.emptyTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        });

        viewModel.getStateLiveData().observe(this, state -> {
            if (state == null) {
                return;
            }

            boolean loading = state.isLoading();
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.logoutButton.setEnabled(!loading);
            binding.createGameButton.setEnabled(!loading);

            if (binding.swipeRefreshLayout.isRefreshing() && !loading) {
                binding.swipeRefreshLayout.setRefreshing(false);
            }

            if (state.getErrorMessage() != null && !state.getErrorMessage().isBlank()) {
                Toast.makeText(this, state.getErrorMessage(), Toast.LENGTH_SHORT).show();
                viewModel.clearError();
                return;
            }

            if (state.isLoggedOut() || state.isUnauthorized()) {
                Intent intent = new Intent(this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return;
            }

            if (state.isOpenCreateGameScreen()) {
                viewModel.clearNavigation();
                startActivity(new Intent(this, CreateGameActivity.class));
                return;
            }

            if (state.getOpenedGameId() != null && !state.getOpenedGameId().isBlank()) {
                String gameId = state.getOpenedGameId();
                viewModel.clearNavigation();

                Intent intent = new Intent(this, CurrentGameActivity.class);
                intent.putExtra(CurrentGameActivity.EXTRA_GAME_ID, gameId);
                startActivity(intent);
            }
        });
    }
}
