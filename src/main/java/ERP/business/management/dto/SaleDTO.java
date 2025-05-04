package ERP.business.management.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleDTO {
    private UUID id;
    private LocalDate saleDate;
    private Float totalValue;
    private UUID customerId;
    private List<SaleItemDTO> items;
}
