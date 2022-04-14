package account.service;

import account.model.SecurityEvent;
import account.repository.SecurityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityService {

    private final SecurityEventRepository securityEventRepository;

    @Autowired
    public SecurityService(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }



    public List<SecurityEvent> getSecurityEvents() {
        return securityEventRepository.findAllByOrderByIdAsc();
    }
}
