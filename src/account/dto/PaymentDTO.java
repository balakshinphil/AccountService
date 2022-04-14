package account.dto;

import account.model.Payment;
import account.model.User;

import java.time.format.DateTimeFormatter;

public class PaymentDTO {
    private final String name;
    private final String lastname;
    private final String period;
    private final String salary;

    public PaymentDTO(Payment payment, User user) {
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.period = payment.getPeriod().format(DateTimeFormatter.ofPattern("MMMM-yyyy"));
        this.salary = String.format("%d dollar(s) %d cent(s)", payment.getSalary() / 100, payment.getSalary() % 100);
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPeriod() {
        return period;
    }

    public String getSalary() {
        return salary;
    }
}
