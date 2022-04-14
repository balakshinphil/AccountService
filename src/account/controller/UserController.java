package account.controller;

import account.dto.UserDTO;
import account.model.User;
import account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }



    @PostMapping("/signup")
    public UserDTO register(@RequestBody @Valid  User user) {
        return userService.register(user);
    }

    @PostMapping("/changepass")
    public Map<String, String> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestBody Map<String, String> passwordInfo) {
        return userService.changePassword(userDetails, passwordInfo);
    }

}
