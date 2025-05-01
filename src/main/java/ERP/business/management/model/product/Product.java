package ERP.business.management.model.product;

import ERP.business.management.model.sale.SaleItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Float price;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(unique = true, nullable = false)
    private String barcode;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> saleItems = new ArrayList<>();
}
