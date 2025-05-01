package ERP.business.management.repositories;

import ERP.business.management.model.customer.Customer;
import ERP.business.management.model.customer.CustomerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository {
    List<Customer> findByCustomerTYpe(CustomerType type);
}
