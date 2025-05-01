package ERP.business.management.model.customer;

import ERP.business.management.model.sale.Sale;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table (name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String email;

    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false)
    private CustomerType customerType;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Sale> sales = new ArrayList<>();
}
