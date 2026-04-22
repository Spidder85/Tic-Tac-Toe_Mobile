package app.presentation.games;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.databinding.ItemAvailableGameBinding;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.GameViewHolder> {
    public interface OnGameClickListener {
        void onGameClick(GameListItemViewData item);
    }

    private final List<GameListItemViewData> items = new ArrayList<>();
    private final OnGameClickListener listener;

    public GamesAdapter(OnGameClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAvailableGameBinding binding = ItemAvailableGameBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new GameViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void submitList(List<GameListItemViewData> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        private final ItemAvailableGameBinding binding;

        public GameViewHolder(ItemAvailableGameBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(GameListItemViewData item, OnGameClickListener listener) {
            binding.gameIdTextView.setText(item.getId());
            binding.creatorLoginTextView.setText(item.getCreatorLogin());
            binding.getRoot().setOnClickListener(view -> listener.onGameClick(item));
        }
    }
}
