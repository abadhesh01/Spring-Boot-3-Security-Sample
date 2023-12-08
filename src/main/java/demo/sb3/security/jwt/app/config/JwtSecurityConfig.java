package demo.sb3.security.jwt.app.config;

import com.google.gson.Gson;
import demo.sb3.security.jwt.app.util.JwtUtil;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@SuppressWarnings("unused")
public class JwtSecurityConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;

    @Bean
    public SecurityFilterChain jwtSecurityFilterChain(HttpSecurity security) throws Exception {
        return security.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/users/register", "/secured_users/**")
                                .permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        applicationContext.getBean("oncePerRequestFilter", OncePerRequestFilter.class),
                        UsernamePasswordAuthenticationFilter.class
                )
                .authenticationProvider(applicationContext.getBean(AuthenticationProvider.class))
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(
                        applicationContext.getBean(AuthenticationEntryPoint.class)))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public OncePerRequestFilter oncePerRequestFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    @Nonnull HttpServletRequest request,
                    @Nonnull HttpServletResponse response,
                    @Nonnull FilterChain filterChain) {
                try {
                    String authorizationHeader = request.getHeader("Authorization");
                    String jwtToken = null;
                    String username = null;
                    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                        jwtToken = authorizationHeader.substring(7);
                        username = jwtUtil.extractUsername(jwtToken);
                    }
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = applicationContext.getBean(UserDetailsService.class)
                                .loadUserByUsername(username);
                        if (jwtUtil.isTokenValid(jwtToken, userDetails)) {
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        }
                    }
                    filterChain.doFilter(request, response);
                } catch (Exception exception) {
                    exceptionResolver.resolveException(request, response, null, exception);
                }
            }
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {

            Map<String, Object> authenticationErrorResponse = new LinkedHashMap<>();
            authenticationErrorResponse.put("error", "Authentication Failed!");
            authenticationErrorResponse.put("cause", authException.getClass().getName());
            authenticationErrorResponse.put("message", authException.getMessage());
            authenticationErrorResponse.put("time", LocalDateTime.now().toString());

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            Gson gson = new Gson();
            String authErrorJsonString = gson.toJson(authenticationErrorResponse);

            PrintWriter responseWriter = response.getWriter();
            responseWriter.write(authErrorJsonString);
            responseWriter.flush();
            responseWriter.close();
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }
}
