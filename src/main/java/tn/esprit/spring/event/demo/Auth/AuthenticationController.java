package tn.esprit.spring.event.demo.Auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.event.demo.Model.Event;
import tn.esprit.spring.event.demo.Model.UserRegisterToken;
import tn.esprit.spring.event.demo.Repository.UserRegisterTokenRepository;
import tn.esprit.spring.event.demo.Service.EventService;
import tn.esprit.spring.event.demo.Service.UserDetailsServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authService ;
    private final UserRegisterTokenRepository userRegisterTokenRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final EventService eventService;

    // -------------------------
    // GET ALL EVENTS
    // -------------------------

    @GetMapping
    public List<Event> all() {
        return eventService.findAll();
    }
    // -------------------------
    // REGISTER
    // -------------------------
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // -------------------------
    // AUTHENTICATE
    // -------------------------
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    // -------------------------
    // REFRESH TOKEN
    // -------------------------
    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }

    // -------------------------
    // LOGOUT
    // -------------------------
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok("Logged out successfully");
    }

    // -------------------------
    // REGISTER CLIENT WITH TOKEN
    // -------------------------
    @PostMapping("/register-client")
    public ResponseEntity<?> registerClient(
            HttpServletRequest request,
            @RequestBody RegisterRequest registerRequest
    ) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
        }

        String token = authHeader.substring(7);

        Optional<UserRegisterToken> userRegisterTokenOpt = userRegisterTokenRepository.findByToken(token);
        if (userRegisterTokenOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid token");
        }

        UserRegisterToken registerToken = userRegisterTokenOpt.get();

        if (userDetailsService.hasExipred(registerToken.getExpiryDateTime())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token expired");
        }

        // Register the client
        AuthenticationResponse response = authService.registerClient(registerRequest);
        return ResponseEntity.ok(response);
    }

    // -------------------------
    // GET DATA BY REGISTRATION TOKEN
    // -------------------------
    @GetMapping("/registration-data/{token}")
    public ResponseEntity<?> getRegistrationData(@PathVariable String token) {

        Optional<UserRegisterToken> userRegisterTokenOpt = userRegisterTokenRepository.findByToken(token);
        if (userRegisterTokenOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid token");
        }

        UserRegisterToken userRegisterToken = userRegisterTokenOpt.get();
        return ResponseEntity.ok(authService.getUserByToken(userRegisterToken));
    }
}
