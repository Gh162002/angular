package tn.esprit.spring.event.demo.Auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.event.demo.Model.*;
import tn.esprit.spring.event.demo.Repository.TokenRepository;
import tn.esprit.spring.event.demo.Repository.UserRegisterTokenRepository;
import tn.esprit.spring.event.demo.Repository.UserRepository;
import tn.esprit.spring.event.demo.config.JwtService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserRegisterTokenRepository userRegisterTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private StringEncryptor stringEncryptor;

    // -------------------------
    // REGISTER (NORMAL)
    // -------------------------
    public AuthenticationResponse register(RegisterRequest request) {

        Role role = determineUserRole(request);

        // Utiliser directement l'objet Address envoyé par le client
        Address address = request.getAddress();

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .address(address)
                .phones(request.getPhones() != null ? request.getPhones() : new ArrayList<>())
                .datebirth(request.getDatebirth())
                .build();

        userRepository.save(user);

        Map<String, Object> extraClaims = Map.of(
                "roles", user.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.toList())
        );

        String jwtToken = jwtService.generateToken(extraClaims, user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }


    private Role determineUserRole(RegisterRequest request) {
        if (request.getEmail() == null) return Role.CLIENT;
        return "admin@gmail.com".equalsIgnoreCase(request.getEmail()) ? Role.ADMIN : Role.CLIENT;
    }

    private Address parseAddress(String addressStr) {
        if (addressStr == null || addressStr.isEmpty()) return null;
        String[] parts = addressStr.split(",");
        if (parts.length < 4) return null;

        return Address.builder()
                .street(parts[0].trim())
                .city(parts[1].trim())
                .state(parts[2].trim())
                .zip(parts[3].trim())
                .build();
    }

    // -------------------------
    // AUTHENTICATE
    // -------------------------
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        revokeAllUserTokens(user);

        Map<String, Object> extraClaims = Map.of(
                "roles", user.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.toList())
        );

        String jwtToken = jwtService.generateToken(extraClaims, user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    // -------------------------
    // REGISTER CLIENT (TOKEN)
    // -------------------------
    public AuthenticationResponse registerClient(RegisterRequest request) {

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CLIENT)
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    // -------------------------
    // GET USER DATA BY TOKEN
    // -------------------------
    public Map<String, Object> getUserByToken(UserRegisterToken token) {

        Map<String, Object> data = new HashMap<>();
        data.put("email", token.getUser().getEmail());
        data.put("firstname", token.getUser().getFirstName());
        data.put("lastname", token.getUser().getLastName());
        data.put("expiryDate", token.getExpiryDateTime());
        data.put("tokenValid", token.getExpiryDateTime().isAfter(LocalDateTime.now()));

        return data;
    }

    // -------------------------
    // TOKEN MANAGEMENT
    // -------------------------
    private void revokeAllUserTokens(User user) {
        var validTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        validTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    // -------------------------
    // REFRESH TOKEN
    // -------------------------
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return;

        String refreshToken = authHeader.substring(7);
        String userEmail = jwtService.ExtractUsername(refreshToken);

        if (userEmail == null) return;

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) return;

        String newAccessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, newAccessToken);

        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();

        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }

    // -------------------------
    // LOGOUT
    // -------------------------
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return;

        String token = authHeader.substring(7);
        String userEmail = jwtService.ExtractUsername(token);

        if (userEmail == null) return;

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (jwtService.isTokenValid(token, user)) {
            revokeAllUserTokens(user);
        }
    }

    // -------------------------
    // ENCRYPT / DECRYPT UTIL
    // -------------------------
    public String encrypt(String plaintext) {
        return stringEncryptor.encrypt(plaintext);
    }

    public String decrypt(String encryptedText) {
        return stringEncryptor.decrypt(encryptedText);
    }
}
