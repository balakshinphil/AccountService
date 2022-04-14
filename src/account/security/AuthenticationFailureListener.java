package account.security;

import account.exception.UserNotFoundException;
import account.model.SecurityEventAction;
import account.model.User;
import account.model.UserRole;
import account.repository.UserRepository;
import account.service.LogSecurityEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Optional;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final HttpServletRequest request;
    private final UserRepository userRepository;
    private final LogSecurityEventService logSecurityEventService;

    @Autowired
    public AuthenticationFailureListener(HttpServletRequest request,
                                         UserRepository userRepository,
                                         LogSecurityEventService logSecurityEventService) {
        this.request = request;
        this.userRepository = userRepository;
        this.logSecurityEventService = logSecurityEventService;
    }



    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        if (request.getHeader("authorization") != null) {
            checkFailedLogin();
        }
    }



    private void checkFailedLogin() {
        String username = getUsername(request.getHeader("authorization"));
        String path = request.getRequestURI();

        logSecurityEventService.log(SecurityEventAction.LOGIN_FAILED, username, path);

        Optional<User> user = userRepository.findUserByUsernameIgnoreCase(username);


        if (user.isPresent()) {
            user.get().addFailedAttempt();
            if (isBruteforce(user.get())) {
                logSecurityEventService.log(SecurityEventAction.BRUTE_FORCE, username, path);
                lockUser(user.get(), path);
            }
            userRepository.save(user.get());
        }
    }

    private String getUsername(String headerAuthorization) {
        String credentials = headerAuthorization.split(" ")[1];
        return new String(Base64.getDecoder().decode(credentials)).split(":")[0];
    }

    private boolean isBruteforce(User user) {
        return user.getFailedAttempts() > 4;
    }

    private void lockUser(User user, String path) {
        if (!user.getRoles().contains(UserRole.ADMINISTRATOR)) {
            user.setAccountNonLocked(false);

            logSecurityEventService.log(SecurityEventAction.LOCK_USER, user.getUsername(),
                    String.format("Lock user %s", user.getUsername()), path);
        }
    }

}
