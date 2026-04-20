package app.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    UserEntity getCurrentUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveUser(UserEntity user);

    @Query("DELETE FROM users")
    void clear();
}
