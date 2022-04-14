package account.security;

import account.model.SecurityEvent;
import account.model.SecurityEventAction;
import account.repository.SecurityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityEventRepository securityEventRepository;

    @Autowired
    public CustomAccessDeniedHandler(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }



    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String username = request.getRemoteUser();
        String path = request.getRequestURI();

        SecurityEvent event = new SecurityEvent(SecurityEventAction.ACCESS_DENIED, username, path, path);
        securityEventRepository.save(event);


        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!");
    }

}
