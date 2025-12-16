package tn.esprit.spring.event.demo.Service;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tn.esprit.spring.event.demo.Model.User;
import tn.esprit.spring.event.demo.Model.UserRegisterToken;
import tn.esprit.spring.event.demo.Repository.UserRegisterTokenRepository;
import tn.esprit.spring.event.demo.Repository.UserRepository;
import tn.esprit.spring.event.demo.Service.UserDetailsServiceImpl;
import tn.esprit.spring.event.demo.config.JwtService;

import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final UserRepository repository;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final UserRegisterTokenRepository userRegisterTokenRepository;


    @Autowired
    private UserDetailsServiceImpl detailsServiceImpl;

    @Autowired
    UserRepository rep;

    public User findByUsername(String email) {
        Optional<User> oUser;
        oUser = rep.findByEmail(email);
        if (oUser.isPresent()) {
            return oUser.get();
        } else {
            return null;
        }
    }

    public ResponseEntity<?> getConnectedUser(HttpServletRequest request,
                                              HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String userEmail;
        final String token;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        token = authHeader.substring(7);
        userEmail = jwtService.ExtractUsername(token);
        if (userEmail != null) {

            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();

            if (jwtService.isTokenValid(token, user)) {
                HashMap<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("email", user.getEmail());
                userData.put("firstname", user.getFirstName());
                userData.put("lastname", user.getLastName());
                userData.put("address", user.getAddress());
                userData.put("telephone", user.getPhones());
                if (user.getDatebirth() != (null)) {
                    userData.put("datebirth", user.getDatebirth().toString());
                }

                userData.put("role", user.getRole());
                return ResponseEntity.ok(userData);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    public ResponseEntity<?> getUser(HttpServletRequest request,
                                     HttpServletResponse response, Long id) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String userEmail;
        final String token;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        token = authHeader.substring(7);
        userEmail = jwtService.ExtractUsername(token);
        if (userEmail != null) {

            var authuser = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            var user = this.repository.findById(id).orElseThrow();
            if (jwtService.isTokenValid(token, authuser)) {
                HashMap<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("email", user.getEmail());
                userData.put("firstname", user.getFirstName());
                userData.put("lastname", user.getLastName());
                userData.put("address", user.getAddress());
                userData.put("telephone", user.getPhones());
                if (user.getDatebirth() != (null)) {
                    userData.put("datebirth", user.getDatebirth().toString());
                }

                userData.put("role", user.getRole());
                return ResponseEntity.ok(userData);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    public String GenerateRegisterToken(User user) {
        String Token = jwtService.generateToken(user);
        UserRegisterToken userRegisterToken = new UserRegisterToken();
        LocalDateTime currentDateTime = LocalDateTime.now();
        userRegisterToken.setUser(user);
        userRegisterToken.setToken(Token);
        LocalDateTime expiryDateTime = currentDateTime.plusDays(1);
        userRegisterToken.setExpiryDateTime(expiryDateTime);
        userRegisterToken = userRegisterTokenRepository.save(userRegisterToken);
        if (Token != null) {
            String URL = "http://localhost:4200/registerClient/";
            return URL += userRegisterToken.getToken();
        } else {
            return null;
        }
    }









}