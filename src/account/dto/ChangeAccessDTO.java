package account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class ChangeAccessDTO {

    @NotBlank
    @JsonProperty("user")
    private String username;

    @Pattern(regexp = "LOCK|UNLOCK")
    private String operation;

    public ChangeAccessDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
