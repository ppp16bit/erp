package ERP.business.management.repositories;

import ERP.business.management.model.sale.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SaleItemRepository extends JpaRepository <SaleItem, UUID>{
    List<SaleItem> findBySaleId(UUID sale_id);
}
