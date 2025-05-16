package ERP.business.management.controllers;

import ERP.business.management.dto.SaleDTO;
import ERP.business.management.services.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @GetMapping
    public ResponseEntity<List<SaleDTO>> getALlSales() {
        return ResponseEntity.ok(saleService.findAll());
    }

    @GetMapping("/{id}")
    private ResponseEntity<SaleDTO> getSaleById(@PathVariable UUID id) {
        return saleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SaleDTO>> getSalesByCustomer(@PathVariable UUID customerId) {
        List<SaleDTO> sales = saleService.findByCustomerId(customerId);
        return ResponseEntity.ok(sales);
    }

    @PostMapping
    public ResponseEntity<SaleDTO> createSale(@RequestBody SaleDTO saleDTO) {
        return saleService.create(saleDTO)
                .map(createSale -> ResponseEntity.status(HttpStatus.CREATED).body(createSale))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleDTO> updateSale(@PathVariable UUID id, @RequestBody SaleDTO saleDTO) {
        return saleService.update(id, saleDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable UUID id) {
        if (saleService.delete(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
