package demo.sb3.security.jwt.app.controller;

import demo.sb3.security.jwt.app.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/secured_users")
@SuppressWarnings("unused")
public class UserLoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginSecuredUser(@RequestBody Map<String, String> loginCredentials) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginCredentials.get("username"),
                        loginCredentials.get("password"))
        );
        if (authentication.isAuthenticated()) {
            Map<String, String> authenticationResponse = new LinkedHashMap<>();
            authenticationResponse.put("message", "Authentication Successful.");
            authenticationResponse.put("JWT", jwtUtil.generateToken(loginCredentials.get("username")));
            return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
        }
        throw new BadCredentialsException("Either 'username' or 'password is invalid!'");
    }

    @GetMapping("/welcome")
    public ResponseEntity<List<String>> logoutSecuredUser() {
        List<String> authenticationResponse = new LinkedList<>();
        authenticationResponse.add("Welcome! :)");
        authenticationResponse.add("This is a sample SpringBoot 3 Security application.");
        authenticationResponse.add("Users are authenticated with JWT(JSON Web Token).");
        authenticationResponse.add("Users are authorized with USER and ADMIN access.");
        authenticationResponse.add("Explore the source code to know more.");
        return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
    }
}
