package account.controller;

import account.model.Payment;
import account.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }



    @GetMapping("/empl/payment")
    public Object getPayments(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam Optional<@Pattern(regexp = "(0\\d|1[0-2])-\\d{4}") String> period) {
        return paymentService.getPayments(userDetails, period);
    }

    @PostMapping("/acct/payments")
    public Map<String, String> addPayments(@RequestBody List<Payment> payments) {
        return paymentService.addPayments(payments);
    }

    @PutMapping("/acct/payments")
    public Map<String, String> changeSalary(@RequestBody @Valid Payment payment) {
        return paymentService.changeSalary(payment);
    }


}
