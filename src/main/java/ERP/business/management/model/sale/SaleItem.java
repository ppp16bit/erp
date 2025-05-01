package ERP.business.management.model.sale;

import ERP.business.management.model.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "sale_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class SaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false)
    private Float unitprice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
