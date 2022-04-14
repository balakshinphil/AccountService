package account.service;

import account.dto.PaymentDTO;
import account.exception.PaymentException;
import account.exception.UserNotFoundException;
import account.model.Payment;
import account.model.User;
import account.repository.PaymentRepository;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(UserRepository userRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }



    public Object getPayments(UserDetails userDetails, Optional<String> period) {
        if (period.isPresent()) {
            return getPaymentForPeriod(userDetails, period.get());
        }
        return getAllPayments(userDetails);
    }

    public Map<String, String> addPayments(List<Payment> payments) {
        validatePayments(payments);

        paymentRepository.saveAll(payments);
        return Map.of("status", "Added successfully!");
    }

    public Map<String, String> changeSalary(Payment payment) {
        Payment oldPayment = getPaymentByUsernameAndPeriod(payment.getEmployee(), payment.getPeriod());

        oldPayment.setSalary(payment.getSalary());

        paymentRepository.save(oldPayment);

        return Map.of("status", "Updated successfully!");
    }



    private PaymentDTO getPaymentForPeriod(UserDetails userDetails, String periodStr) {
        User user = getUserByUsername(userDetails.getUsername());
        LocalDate period = getPeriodFromString(periodStr);
        Payment payment = getPaymentByUsernameAndPeriod(user.getUsername(), period);

        return new PaymentDTO(payment, user);
    }

    private LocalDate getPeriodFromString(String period) {
        return YearMonth.parse(period, DateTimeFormatter.ofPattern("MM-yyyy")).atDay(1);
    }

    private List<PaymentDTO> getAllPayments(UserDetails userDetails) {
        User user = getUserByUsername(userDetails.getUsername());
        List<Payment> payments = paymentRepository.findAllByEmployeeOrderByPeriodDesc(user.getUsername());

        return payments.stream().map(payment -> new PaymentDTO(payment, user)).collect(Collectors.toList());
    }

    private User getUserByUsername(String username) {
        Optional<User> user = userRepository.findUserByUsernameIgnoreCase(username);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }

        return user.get();
    }

    private void validatePayments(List<Payment> payments) {
        List<Payment> validatedPayments = new ArrayList<>();

        for (Payment payment :payments) {
            checkIfPaymentUniqueInRepository(payment);
            checkIfPaymentUniqueInCurrentPayments(validatedPayments, payment);
            checkIfEmployeeExists(payment);
            validatedPayments.add(payment);
        }
    }

    private void checkIfPaymentUniqueInRepository(Payment payment) {
        if (paymentRepository.existsPaymentByPeriod(payment.getPeriod())) {
            throw new PaymentException("Exists payment for this period");
        }
    }

    private void checkIfPaymentUniqueInCurrentPayments(List<Payment> currentPayments, Payment payment) {
        if (currentPayments.stream().anyMatch(p -> Objects.equals(p.getEmployee(), payment.getEmployee()) &&
                p.getPeriod() == payment.getPeriod())) {
            throw new PaymentException("Exists payment for this period");
        }
    }

    private void checkIfEmployeeExists(Payment payment) {
        Optional<User> employee = userRepository.findUserByUsernameIgnoreCase(payment.getEmployee());
        if (employee.isEmpty()) {
            throw new PaymentException("No such employee");
        }
    }

    private Payment getPaymentByUsernameAndPeriod(String username, LocalDate period) {
        Optional<Payment> payment = paymentRepository.findPaymentByEmployeeAndPeriod(username, period);

        if (payment.isEmpty()) {
            throw new PaymentException("Payment not found");
        }

        return payment.get();
    }

}
