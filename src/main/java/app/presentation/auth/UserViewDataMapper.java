package app.presentation.auth;

import app.domain.model.User;

public class UserViewDataMapper {
    public UserViewData fromDomain(User user) {
        if (user == null) {
            return null;
        }

        return new UserViewData(
                user.getId(),
                user.getLogin()
        );
    }

    public User toDomain(UserViewData userViewData, String password) {
        if (userViewData == null) {
            return null;
        }

        return new User(
                userViewData.getId(),
                userViewData.getLogin(),
                password
        );
    }
}
