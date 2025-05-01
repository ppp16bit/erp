package ERP.business.management.repositories;

import ERP.business.management.model.sale.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {
    List<Sale> findByCustomerId(UUID customer_id);
}
