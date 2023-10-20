package pwr.zpibackend.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class GoogleTokenFilter extends OncePerRequestFilter {

    public static final String AUTHENTICATION_HEADER = "Authorization";
    public static final String AUTHENTICATION_HEADER_TOKEN_PREFIX = "Bearer ";

    private static final String OPENAPI_DOCS_URL = "/v3/api-docs";
    private static final String SWAGGER_UI_URL = "/swagger-ui/";
    private final GoogleAuthService googleAuthService;

    public GoogleTokenFilter(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authenticationHeader = request.getHeader(AUTHENTICATION_HEADER);

        String path = request.getRequestURI();
        if (path.startsWith(SWAGGER_UI_URL) || path.startsWith(OPENAPI_DOCS_URL)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (authenticationHeader == null || !authenticationHeader.startsWith(AUTHENTICATION_HEADER_TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            GoogleIdToken token = validateTokenFromHeader(authenticationHeader);

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            GoogleUser.fromGoogleTokenPayload(token.getPayload()),
                            null, null));
        } catch (GeneralSecurityException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token!");
        }

        filterChain.doFilter(request, response);


    }


    private GoogleIdToken validateTokenFromHeader(String authenticationHeader) throws GeneralSecurityException,
            IOException {
        int authenticationHeaderPrefixLength = AUTHENTICATION_HEADER_TOKEN_PREFIX.length();
        String token = authenticationHeader.substring(authenticationHeaderPrefixLength);
        return googleAuthService.validateToken(token);
    }
}
