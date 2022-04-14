package account.controller;

import account.dto.ChangeAccessDTO;
import account.dto.ChangeRoleDTO;
import account.dto.UserDTO;
import account.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }



    @GetMapping("/user")
    public List<UserDTO> getUsers() {
        return adminService.getAllUsers();
    }

    @PutMapping("/user/role")
    public UserDTO changeRole(@RequestBody ChangeRoleDTO changeRoleDTO) {
        return adminService.changeRole(changeRoleDTO);
    }

    @PutMapping("/user/access")
    public Map<String, String> changeAccess(@RequestBody ChangeAccessDTO changeAccessDTO) {
        return adminService.changeAccess(changeAccessDTO);
    }

    @DeleteMapping("/user/{email}")
    public Map<String, String> deleteUser(@PathVariable String email) {
        return adminService.deleteUser(email);
    }

}
