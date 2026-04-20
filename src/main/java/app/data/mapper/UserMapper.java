package app.data.mapper;

import app.data.local.UserEntity;
import app.domain.model.User;

public class UserMapper {
    public User fromEntity(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return new User(
                entity.id,
                entity.login,
                entity.password
        );
    }

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId(),
                user.getLogin(),
                user.getPassword()
        );
    }
}
