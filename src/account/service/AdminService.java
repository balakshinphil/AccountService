package account.service;

import account.dto.ChangeAccessDTO;
import account.dto.ChangeRoleDTO;
import account.dto.UserDTO;
import account.exception.AdminException;
import account.exception.UserNotFoundException;
import account.model.SecurityEventAction;
import account.model.User;
import account.model.UserRole;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final LogSecurityEventService logSecurityEventService;


    @Autowired
    public AdminService(UserRepository userRepository, LogSecurityEventService logSecurityEventService) {
        this.userRepository = userRepository;
        this.logSecurityEventService = logSecurityEventService;
    }



    public List<UserDTO> getAllUsers() {
        return userRepository.findAllByOrderByIdAsc().stream().map(UserDTO::new).collect(Collectors.toList());
    }

    public UserDTO changeRole(ChangeRoleDTO changeRoleDTO) {

        User user = getUserByUsername(changeRoleDTO.getUsername());

        switch (changeRoleDTO.getOperation()) {
            case "GRANT":
                grantRole(user, changeRoleDTO.getRole());
                break;
            case "REMOVE":
                removeRole(user, changeRoleDTO.getRole());
                break;
            default:
                throw new AdminException("Invalid operation!");
        }

        return new UserDTO(userRepository.save(user));
    }

    public Map<String, String> changeAccess(ChangeAccessDTO changeAccessDTO) {
        User user = getUserByUsername(changeAccessDTO.getUsername());

        switch (changeAccessDTO.getOperation()) {
            case "LOCK":
                return lockUser(user);
            case "UNLOCK":
                return unlockUser(user);
            default:
                throw new AdminException("Invalid operation");
        }

    }

    public Map<String, String> deleteUser(String username) {
        User user = getUserByUsername(username);

        if (user.getRoles().contains(UserRole.ADMINISTRATOR)) {
            throw new AdminException("Can't remove ADMINISTRATOR role!");
        }

        userRepository.delete(user);

        logSecurityEventService.log(SecurityEventAction.DELETE_USER, getAdminUsername(),
                user.getUsername(), "/api/admin/user");

        return Map.of(
                "user", username,
                "status", "Deleted successfully!"
        );
    }



    private void grantRole(User user, UserRole role) {
        if (user.getRoles().contains(role)) {
            throw new AdminException("User already has the role!");
        }

        if (user.getRoles().contains(UserRole.ADMINISTRATOR) || role == UserRole.ADMINISTRATOR) {
            throw new AdminException("The user cannot combine administrative and business roles!");
        }

        logSecurityEventService.log(SecurityEventAction.GRANT_ROLE, getAdminUsername(),
                String.format("Grant role %s to %s", role.name(), user.getUsername()), "/api/admin/user/role");

        user.grantAuthority(role);
    }

    private void removeRole(User user, UserRole role) {
        if (role == UserRole.ADMINISTRATOR) {
            throw new AdminException("Can't remove ADMINISTRATOR role!");
        }
        if (!user.getRoles().contains(role)) {
            throw new AdminException("The user does not have a role!");
        }
        if (user.getRoles().size() == 1) {
            throw new AdminException("The user must have at least one role!");
        }

        logSecurityEventService.log(SecurityEventAction.REMOVE_ROLE, getAdminUsername(),
                String.format("Remove role %s from %s", role.name(), user.getUsername()), "/api/admin/user/role");

        user.removeAuthority(role);
    }

    private Map<String, String> lockUser(User user) {
        if (user.getRoles().contains(UserRole.ADMINISTRATOR)) {
            throw new AdminException("Can't lock the ADMINISTRATOR!");
        }

        user.setAccountNonLocked(false);
        userRepository.save(user);

        logSecurityEventService.log(SecurityEventAction.LOCK_USER, getAdminUsername(),
                String.format("Lock user %s", user.getUsername()), "/api/admin/user/access");

        return Map.of("status", String.format("User %s locked!", user.getUsername()));
    }

    private Map<String, String> unlockUser(User user) {
        user.setAccountNonLocked(true);
        user.resetFailedAttempts();
        userRepository.save(user);

        logSecurityEventService.log(SecurityEventAction.UNLOCK_USER, getAdminUsername(),
                String.format("Unlock user %s", user.getUsername()), "/api/admin/user/access");

        return Map.of("status", String.format("User %s unlocked!", user.getUsername()));
    }

    private User getUserByUsername(String username) {
        Optional<User> user = userRepository.findUserByUsernameIgnoreCase(username);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }

        return user.get();
    }

    private String getAdminUsername() {
        Optional<User> admin = userRepository.findAll()
                .stream().filter(user -> user.getRoles().contains(UserRole.ADMINISTRATOR)).findFirst();
        if (admin.isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }

        return admin.get().getUsername();
    }
}
