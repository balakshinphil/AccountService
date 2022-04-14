package account.repository;

import account.model.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {
    boolean existsPaymentByPeriod(LocalDate period);

    Optional<Payment> findPaymentByEmployeeAndPeriod(String employee, LocalDate period);

    List<Payment> findAllByEmployeeOrderByPeriodDesc(String employee);
}
