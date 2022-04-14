package account.service;

import account.dto.UserDTO;
import account.exception.UserExistsException;
import account.exception.PasswordException;
import account.exception.UserNotFoundException;
import account.model.SecurityEventAction;
import account.model.User;
import account.model.UserRole;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogSecurityEventService logSecurityEventService;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       LogSecurityEventService logSecurityEventService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.logSecurityEventService = logSecurityEventService;
    }



    public UserDTO register(User user) {
        checkIfUserExists(user);

        validatePassword(user.getPassword());

        user.setUsername(user.getUsername().toLowerCase());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        assignUserRole(user);

        logSecurityEventService.log(SecurityEventAction.CREATE_USER, "Anonymous",
                user.getUsername(), "/api/auth/signup");

        return new UserDTO(userRepository.save(user));
    }

    public Map<String, String> changePassword(UserDetails userDetails, Map<String, String> passwordInfo) {
        String password = passwordInfo.get("new_password");

        validatePassword(password);

        checkIfNewPasswordDiffers(userDetails.getPassword(), password);


        User user = getUserByUsername(userDetails.getUsername());

        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        logSecurityEventService.log(SecurityEventAction.CHANGE_PASSWORD, user.getUsername(),
                user.getUsername(), "/api/auth/changepass");

        return Map.of(
                "email", user.getUsername(),
                "status", "The password has been updated successfully"
        );
    }



    private void checkIfUserExists(User user) {
        if (userRepository.findUserByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            throw new UserExistsException("User exist!");
        }
    }

    private void validatePassword(String password) {
        List<String> breachedPasswords = List.of("PasswordForJanuary", "PasswordForFebruary",
                "PasswordForMarch", "PasswordForApril", "PasswordForMay", "PasswordForJune", "PasswordForJuly",
                "PasswordForAugust", "PasswordForSeptember", "PasswordForOctober",
                "PasswordForNovember", "PasswordForDecember");

        if (password == null) {
            throw new PasswordException("Null password!");
        }

        if (breachedPasswords.contains(password)) {
            throw new PasswordException("The password is in the hacker's database!");
        }

        if (password.length() < 12) {
            throw new PasswordException("Password length must be 12 chars minimum!");
        }
    }

    private void assignUserRole(User user) {
        if (userRepository.findAll().isEmpty()) {
            user.getRoles().clear();
            user.grantAuthority(UserRole.ADMINISTRATOR);
        } else {
            user.grantAuthority(UserRole.USER);
        }
    }

    private void checkIfNewPasswordDiffers(String oldPassword, String newPassword) {
        if (passwordEncoder.matches(oldPassword, newPassword)) {
            throw new PasswordException("The passwords must be different!");
        }
    }

    private User getUserByUsername(String username) {
        Optional<User> user = userRepository.findUserByUsernameIgnoreCase(username);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }

        return user.get();
    }
}
