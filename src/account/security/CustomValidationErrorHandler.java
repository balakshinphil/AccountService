package account.security;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;

@ControllerAdvice
public class CustomValidationErrorHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolationException(ConstraintViolationException e,
                                                   ServletWebRequest webRequest) throws IOException {
        assert webRequest.getResponse() != null;
        webRequest.getResponse().sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
}
