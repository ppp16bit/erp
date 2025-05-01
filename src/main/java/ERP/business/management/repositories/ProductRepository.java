package ERP.business.management.repositories;

import ERP.business.management.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository {
    Optional<Product> findByBarcode(String barcode);
}
