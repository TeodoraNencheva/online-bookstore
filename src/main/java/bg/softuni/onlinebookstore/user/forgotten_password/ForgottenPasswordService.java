package bg.softuni.onlinebookstore.user.forgotten_password;

import bg.softuni.onlinebookstore.model.email.ForgotPasswordEmailContext;
import bg.softuni.onlinebookstore.model.entity.SecureTokenEntity;
import bg.softuni.onlinebookstore.model.entity.UserEntity;
import bg.softuni.onlinebookstore.model.error.InvalidTokenException;
import bg.softuni.onlinebookstore.repositories.SecureTokenRepository;
import bg.softuni.onlinebookstore.repositories.UserRepository;
import bg.softuni.onlinebookstore.service.EmailService;
import bg.softuni.onlinebookstore.service.SecureTokenService;
import bg.softuni.onlinebookstore.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Optional;

@Service
public class ForgottenPasswordService {
    private final UserService userService;
    private final SecureTokenService secureTokenService;
    private final SecureTokenRepository secureTokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${site.base.url}")
    private String baseURL;

    public ForgottenPasswordService(UserService userService, SecureTokenService secureTokenService, SecureTokenRepository secureTokenRepository, EmailService emailService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.secureTokenService = secureTokenService;
        this.secureTokenRepository = secureTokenRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void forgottenPassword(String email) {
        UserEntity user = userService.getUser(email);
        sendResetPasswordEmail(user);
    }

    private void sendResetPasswordEmail(UserEntity user) {
        SecureTokenEntity secureToken = secureTokenService.createSecureToken();
        secureToken.setUser(user);
        secureTokenRepository.save(secureToken);

        ForgotPasswordEmailContext emailContext = new ForgotPasswordEmailContext();
        emailContext.init(user);
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());

        try {
            emailService.sendEmail(emailContext);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void updatePassword(String password, String token) {
        Optional<SecureTokenEntity> tokenOpt = secureTokenRepository.findByToken(token);

        if (tokenOpt.isEmpty() || tokenOpt.get().isExpired()) {
            throw new InvalidTokenException("Token is invalid.");
        }

        SecureTokenEntity secureToken = tokenOpt.get();
        UserEntity user = secureToken.getUser();
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        secureTokenRepository.delete(secureToken);
    }
}
