package ERP.business.management.dto;

import ERP.business.management.model.customer.CustomerType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private CustomerType customerType;
}
