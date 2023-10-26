package pwr.zpibackend.services;

import lombok.AllArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Role;
import pwr.zpibackend.models.Student;

import java.util.*;


@Service("userDetailsService")
@Transactional
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthService authService;
    private static final Logger logger = LogManager.getLogger(CustomUserDetailsService.class);

    private UserDetails createUserDetails(Object user) {
        String username = null;
        if (user instanceof Employee) {
            username = ((Employee) user).getMail();
        } else if (user instanceof Student) {
            username = ((Student) user).getMail();
        }

        if (username == null) {
            return null;
        }

        logger.info("Creating user details for: " + username);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username,
                null,
                getAuthorities(user)
        );

        logger.info("Created user details: " + userDetails);

        return userDetails;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        logger.info("Loading user by username: " + email);
        Object user = null;
        try {
            user = authService.getUserDetails(email);
        } catch (Exception ignored) {}
        logger.info("Loaded user: " + user);
        return createUserDetails(user);
    }

    private Collection<GrantedAuthority> getAuthorities(Object user) {
        List<Role> roles = null;

        if (user instanceof Employee) {
            roles = ((Employee) user).getRoles();
        } else if (user instanceof Student) {
            roles = new ArrayList<>();
            roles.add(((Student) user).getRole());
        }

        logger.info("Getting authorities for: " + user);

        if (roles == null) {
            return Collections.emptyList();
        }

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        logger.info("Got authorities: " + authorities);

        return authorities;
    }
}
