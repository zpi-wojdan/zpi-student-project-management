package pwr.zpibackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {

    private final FilterChainExceptionHandler filterChainExceptionHandler;

    private final GoogleTokenFilter googleTokenFilter;

    public WebSecurityConfiguration(FilterChainExceptionHandler filterChainExceptionHandler,
                                    GoogleTokenFilter googleTokenFilter) {
        this.filterChainExceptionHandler = filterChainExceptionHandler;
        this.googleTokenFilter = googleTokenFilter;
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable();

        http.authorizeRequests()
                .anyRequest().permitAll();

        http.addFilterBefore(googleTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(filterChainExceptionHandler, GoogleTokenFilter.class)
                .sessionManagement().sessionCreationPolicy(STATELESS);

        http.headers().frameOptions().disable();
        http.requiresChannel(channel -> 
                channel.anyRequest().requiresSecure());
        return http.build();
    }

}
