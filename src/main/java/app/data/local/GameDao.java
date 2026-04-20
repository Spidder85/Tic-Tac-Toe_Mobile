package app.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameDao {
    @Query("SELECT * FROM games")
    List<GameEntity> getGames();

    @Query("SELECT * FROM games WHERE id = :gameId LIMIT 1")
    GameEntity getGameById(String gameId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveGame(GameEntity game);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveGames(List<GameEntity> games);

    @Query("DELETE FROM games")
    void clear();
}
