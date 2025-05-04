package ERP.business.management.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemDTO {
    private UUID id;
    private int quantity;
    private Float unitPrice;
    private UUID productId;
}
