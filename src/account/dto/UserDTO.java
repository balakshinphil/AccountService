package account.dto;

import account.model.User;
import account.model.UserRole;

import java.util.List;
import java.util.stream.Collectors;

public class UserDTO {

    private final long id;
    private final String name;
    private final String lastname;
    private final String email;
    private final List<String> roles;


    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getUsername();
        this.roles = user.getRoles().stream().map(UserRole::toString).sorted().collect(Collectors.toList());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }
}
