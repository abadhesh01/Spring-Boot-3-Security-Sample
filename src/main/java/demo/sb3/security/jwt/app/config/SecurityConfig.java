package demo.sb3.security.jwt.app.config;

import demo.sb3.security.jwt.app.entity.SecuredUser;
import demo.sb3.security.jwt.app.entity.error.SecuredUserNotFoundException;
import demo.sb3.security.jwt.app.repository.SecuredUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@SuppressWarnings("unused")
public class SecurityConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SecuredUserRepository repository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/users/register").permitAll()
                                .requestMatchers("/users/**").authenticated()
                )
                .authenticationProvider(applicationContext.getBean(AuthenticationProvider.class))
                .formLogin(formLogin -> {})
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(applicationContext.getBean(UserDetailsService.class));
        authenticationProvider.setPasswordEncoder(applicationContext.getBean(PasswordEncoder.class));
        return authenticationProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return (username) -> {
            Optional<SecuredUser> user = repository.getUserByUsername(username);
            if (user.isPresent()) return userDetailsOfSecuredUser(user.get());
            else throw new SecuredUserNotFoundException("username", username);
        };
    }

    @Bean
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    public UserDetails userDetailsOfSecuredUser(SecuredUser user) {

        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority(user.getRole().name()));
            }

            @Override
            public String getPassword() {
                return user.getPassword();
            }

            @Override
            public String getUsername() {
                return user.getUsername();
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }

}
