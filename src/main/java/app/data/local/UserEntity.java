package app.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey
    @NonNull
    public String id;

    public String login;
    public String password;

    public UserEntity(@NonNull String id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }
}
