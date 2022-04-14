package account.dto;

import account.model.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class ChangeRoleDTO {

    @NotBlank
    @JsonProperty("user")
    private String username;

    @NotNull
    private UserRole role;

    @Pattern(regexp = "GRANT|REMOVE")
    private String operation;

    public ChangeRoleDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
