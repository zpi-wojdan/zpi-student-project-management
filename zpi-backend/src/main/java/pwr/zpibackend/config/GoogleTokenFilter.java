package pwr.zpibackend.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.user.Role;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.services.impl.user.EmployeeService;
import pwr.zpibackend.services.impl.user.StudentService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class GoogleTokenFilter extends OncePerRequestFilter {

    public static final String AUTHENTICATION_HEADER = "Authorization";
    public static final String AUTHENTICATION_HEADER_TOKEN_PREFIX = "Bearer ";

    private static final String OPENAPI_DOCS_URL = "/v3/api-docs";
    private static final String SWAGGER_UI_URL = "/swagger-ui/";
    private final GoogleAuthService googleAuthService;

    private final EmployeeService employeeService;

    private final StudentService studentService;

    public GoogleTokenFilter(GoogleAuthService googleAuthService, EmployeeService employeeService,
                             StudentService studentService) {
        this.googleAuthService = googleAuthService;
        this.employeeService = employeeService;
        this.studentService = studentService;
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
            GoogleUser googleUser = GoogleUser.fromGoogleTokenPayload(token.getPayload());
            String email = googleUser.getEmail();

            if (!isEmailValid(email)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email!");
            }

            if (!isUserInDatabase(email)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "User not in database or user is both student and employee!");
            }

            ArrayList<GrantedAuthority> authorities = getAuthorities(email);

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            GoogleUser.fromGoogleTokenPayload(token.getPayload()),
                            null, authorities));

            request.setAttribute("googleEmail", email);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token!");
        }

        filterChain.doFilter(request, response);
    }

    private boolean isEmailValid(String email) {
        return email != null && Pattern.matches("^[a-z0-9-]{1,50}(\\.[a-z0-9-]{1,50}){0,4}@(?:student\\.)" +
                "?(pwr\\.edu\\.pl|pwr\\.wroc\\.pl)$", email);
    }

    private boolean isUserInDatabase(String email) {
        return employeeService.exists(email) ^ studentService.exists(email);
    }

    private ArrayList<GrantedAuthority> getAuthorities(String email) {
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        try {
            Employee employee = employeeService.getEmployee(email);
            List<Role> roles = employee.getRoles();
            for (Role role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));
            }
        } catch (Exception ignored) {
            try {
                Student student = studentService.getStudent(email);
                authorities.add(new SimpleGrantedAuthority("ROLE_" + student.getRole().getName().toUpperCase()));
            } catch (Exception ignored2) {}
        }
        return authorities;
    }


    private GoogleIdToken validateTokenFromHeader(String authenticationHeader) throws GeneralSecurityException,
            IOException {
        int authenticationHeaderPrefixLength = AUTHENTICATION_HEADER_TOKEN_PREFIX.length();
        String token = authenticationHeader.substring(authenticationHeaderPrefixLength);
        return googleAuthService.validateToken(token);
    }
}
