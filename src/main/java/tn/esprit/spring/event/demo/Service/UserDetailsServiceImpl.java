package tn.esprit.spring.event.demo.Service;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.event.demo.Model.PasswordResetToken;
import tn.esprit.spring.event.demo.Model.User;
import tn.esprit.spring.event.demo.Model.UserRegisterToken;
import tn.esprit.spring.event.demo.Repository.PasswordResetTokenRepository;
import tn.esprit.spring.event.demo.Repository.UserRegisterTokenRepository;
import tn.esprit.spring.event.demo.Repository.UserRepository;
import tn.esprit.spring.event.demo.config.JwtService;

@Service

public class UserDetailsServiceImpl  implements UserDetailsService {
    private final JwtService jwtService = new JwtService();
    @Autowired
    UserRepository userRepository;
    @Autowired
    private StringEncryptor stringEncryptor;
    @Autowired
    private UserRegisterTokenRepository userRegisterTokenRepository;
    private static final String RESET_PASSWORD_EMAIL_TEMPLATE_PATH = "templates/reset_password_email.html";
    private static final String RESET_PASSWORD_EMAIL_TEMPLATE_PATH_FR = "templates/reset_password_email_FR.html";

    private static final String ACTIVATE_ACCOUNT_CLIENT_TEMPLATE_PATH_FR ="templates/account_client_email_FR.html";
    private static final String ACTIVATE_ACCOUNT_CLIENT_TEMPLATE_PATH_EN ="templates/account_client_email_EN.html";

    private static final String CONFIRM_EMAIL_TEMPLATE_PATH_FR="templates/email_confirmation_email_FR.html";
    private static final String CONFIRM_EMAIL_TEMPLATE_PATH_EN="templates/email_confirmation_email_EN.html";

    private static final String ACCOUNT_ACTIVATION_EMAIL_TEMPLATE_PATH_FR="templates/account_activation_email_FR.html";
    private static final String ACCOUNT_ACTIVATION_EMAIL_TEMPLATE_PATH_EN="templates/account_activation_email_EN.html";

    private static final String URL_RESET_PASSWORD = "http://localhost:4200/resetPassword/";
    private static final String URL_REGISTER_CLIENT = "http://localhost:4200/registerClient/";
    private static final String APPLICATION_URL = "http://localhost:4200";



    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        User user = userOptional.get();
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                new HashSet<GrantedAuthority>());
    }

    public String GenerateResetToken(User user){
        String Token = jwtService.generateToken(user);
        PasswordResetToken passwordResetToken= new PasswordResetToken();
        LocalDateTime currentDateTime = LocalDateTime.now();
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(Token);
        LocalDateTime expiryDateTime = currentDateTime.plusMinutes(30);
        passwordResetToken.setExpiryDateTime(expiryDateTime);
        passwordResetToken =  passwordResetTokenRepository.save(passwordResetToken);
        if (Token!=null){
            //  String URL ="http://localhost:4200/resetPassword/";

            return  URL_RESET_PASSWORD+passwordResetToken.getToken() ;
        }else{
            return null;
        }
    }



    public boolean hasExipred(LocalDateTime expiryDateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return expiryDateTime.isAfter(currentDateTime);
    }
    public String encrypt(String plaintext) {
        return stringEncryptor.encrypt(plaintext);
    }

    public String decrypt(String encryptedText) {
        return stringEncryptor.decrypt(encryptedText);
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
            // String URL = "http://localhost:4200/registerClient/";
            return URL_REGISTER_CLIENT + userRegisterToken.getToken();
        } else {
            return null;
        }
    }


}

