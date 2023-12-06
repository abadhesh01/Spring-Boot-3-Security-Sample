package demo.sb3.security.jwt.app.controller;

import demo.sb3.security.jwt.app.entity.SecuredUser;
import demo.sb3.security.jwt.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@SuppressWarnings("unused")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/now")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Principal principal) {
        return new ResponseEntity<>(this.service.getCurrentUser(principal), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody SecuredUser user) {
        return new ResponseEntity<>(this.service.createUser(user), HttpStatus.CREATED);
    }

    @PutMapping("/update/password")
    public ResponseEntity<Map<String, Object>> updateUserPassword(
            @RequestBody Map<String, String> password, Principal principal) {
        return new ResponseEntity<>(this.service.updateUserPassword(password, principal), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("id") UUID id, Principal principal) {
        return new ResponseEntity<>(this.service.deleteUser(id, principal), HttpStatus.OK);
    }

    @GetMapping("/all/users")
    public ResponseEntity<Map<String, Object>> getUsersOnly() {
        return new ResponseEntity<>(this.service.getUsersOnly(), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        return new ResponseEntity<>(this.service.getAllUsers(), HttpStatus.OK);
    }
}
