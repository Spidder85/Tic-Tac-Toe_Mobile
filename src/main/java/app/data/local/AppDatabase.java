package app.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {
                UserEntity.class,
                GameEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    public abstract GameDao gameDao();
}
