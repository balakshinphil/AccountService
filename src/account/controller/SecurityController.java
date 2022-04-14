package account.controller;

import account.model.SecurityEvent;
import account.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SecurityController {

    private final SecurityService securityService;

    @Autowired
    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }



    @GetMapping("/api/security/events")
    public List<SecurityEvent> getSecurityEvents() {
        return securityService.getSecurityEvents();
    }
}
