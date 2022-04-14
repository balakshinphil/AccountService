package account.service;

import account.model.SecurityEvent;
import account.model.SecurityEventAction;
import account.repository.SecurityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogSecurityEventService {

    private final SecurityEventRepository securityEventRepository;

    @Autowired
    public LogSecurityEventService(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }

    public void log(SecurityEventAction action, String subject, String object, String path) {
        SecurityEvent event = new SecurityEvent(action, subject, object, path);
        securityEventRepository.save(event);
    }

    public void log(SecurityEventAction action, String subject, String path) {
        SecurityEvent event = new SecurityEvent(action, subject, path, path);
        securityEventRepository.save(event);
    }
}
