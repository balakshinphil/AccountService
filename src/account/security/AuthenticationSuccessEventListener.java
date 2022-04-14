package account.security;

import account.exception.UserNotFoundException;
import account.model.User;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Optional;

@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final HttpServletRequest request;
    private final UserRepository userRepository;

    @Autowired
    public AuthenticationSuccessEventListener(HttpServletRequest request, UserRepository userRepository) {
        this.request = request;
        this.userRepository = userRepository;
    }



    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String username = getUsername(request.getHeader("authorization"));
        User user = getUserByUsername(username);

        user.resetFailedAttempts();
        userRepository.save(user);
    }



    private String getUsername(String headerAuthorization) {
        String credentials = headerAuthorization.split(" ")[1];
        return new String(Base64.getDecoder().decode(credentials)).split(":")[0];
    }

    private User getUserByUsername(String username) {
        Optional<User> user = userRepository.findUserByUsernameIgnoreCase(username);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }

        return user.get();
    }
}
