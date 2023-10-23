package pwr.zpibackend.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.Getter;

@Getter
public class GoogleUser {
    private final String userId;
    private final String email;

    public GoogleUser(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public static GoogleUser fromGoogleTokenPayload(GoogleIdToken.Payload payload) {
        return new GoogleUser(
                payload.getUserId(),
                payload.getEmail()
        );
    }
}
