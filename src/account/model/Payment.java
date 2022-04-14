package account.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.core.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "PAYMENT")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String employee;

    @NonNull
    private LocalDate period;

    @Min(1)
    private long salary;

    public Payment() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate period) {
        this.period = period;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }



    @JsonProperty("period")
    public void setStrAsPeriod(String period) {
        this.period = YearMonth.parse(period, DateTimeFormatter.ofPattern("MM-yyyy")).atDay(1);
    }
}
