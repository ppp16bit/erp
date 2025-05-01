package ERP.business.management.repositories;

import ERP.business.management.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository <Product, UUID> {
    Optional<Product> findByBarcode(String barcode);
}
