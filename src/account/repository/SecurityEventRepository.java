package account.repository;

import account.model.SecurityEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecurityEventRepository extends CrudRepository<SecurityEvent, Long> {
    List<SecurityEvent> findAllByOrderByIdAsc();
}
