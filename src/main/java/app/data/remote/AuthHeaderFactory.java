package app.data.remote;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

public class AuthHeaderFactory {
    public String create(String login, String password) {
        String rawCredentials = login + ":" + password;
        String encodedCredentials = Base64.encodeToString(
                rawCredentials.getBytes(StandardCharsets.UTF_8),
                Base64.NO_WRAP
        );
        return "Basic " + encodedCredentials;
    }
}
