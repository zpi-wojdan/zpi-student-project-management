package pwr.zpibackend.config;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleAuthService {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    public GoogleIdToken validateToken(String token) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier =
                new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                        new GsonFactory()).setAudience(Collections.singletonList(clientId))
                        .build();

        GoogleIdToken idToken = verifier.verify(token);
        if (idToken == null) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid token!");
        }
        return idToken;
    }
}
