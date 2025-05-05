package ERP.business.management.services;

import ERP.business.management.dto.SaleDTO;
import ERP.business.management.dto.SaleItemDTO;
import ERP.business.management.model.customer.Customer;
import ERP.business.management.model.product.Product;
import ERP.business.management.model.sale.Sale;
import ERP.business.management.model.sale.SaleItem;
import ERP.business.management.repositories.CustomerRepository;
import ERP.business.management.repositories.ProductRepository;
import ERP.business.management.repositories.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<SaleDTO> findAll() {
        return saleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<SaleDTO> findById(UUID id) {
        return saleRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional
    public List<SaleDTO> findByCustomerId(UUID customerId) {
        return saleRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<SaleDTO> create(SaleDTO saleDTO) {
        Optional<Customer> customerOptional = customerRepository.findById(saleDTO.getCustomerId());

        if (customerOptional.isEmpty()) {
            return Optional.empty();
        }

        Customer customer = customerOptional.get();

        Sale sale = Sale.builder()
                .saleDate(saleDTO.getSaleDate() != null ? saleDTO.getSaleDate() : LocalDate.now())
                .customer(customer)
                .items(new ArrayList<>())
                .build();

        float totalValue = 0.0f;

        for (SaleItemDTO itemDTO : saleDTO.getItems()) {
            Optional<Product> productOptional = productRepository.findById(itemDTO.getProductId());

            if (productOptional.isEmpty()) {
                return Optional.empty();
            }

            Product product = productOptional.get();

            if (product.getStockQuantity() < itemDTO.getQuantity()) {
                return Optional.empty();
            }

            SaleItem saleItem = SaleItem.builder()
                    .quantity(itemDTO.getQuantity())
                    .unitprice(itemDTO.getUnitPrice() != null ? itemDTO.getUnitPrice() : product.getPrice())
                    .product(product)
                    .sale(sale)
                    .build();

            productService.updateStock(product.getId(), -itemDTO.getQuantity());

            totalValue += saleItem.getUnitprice() * saleItem.getQuantity();

            sale.getItems().add(saleItem);
        }

        sale.setTotalValue(totalValue);

        Sale savedSale = saleRepository.save(sale);

        return Optional.of(convertToDTO(savedSale));
    }

    @Transactional
    public boolean delete(UUID id) {
        Optional<Sale> saleOptional = saleRepository.findById(id);

        if (saleOptional.isEmpty()) {
            return false;
        }

        Sale sale = saleOptional.get();

        for (SaleItem item : sale.getItems()) {
            productService.updateStock(item.getProduct().getId(), item.getQuantity());
        }

        saleRepository.deleteById(id);
        return true;
    }

    private SaleDTO convertToDTO(Sale sale) {
        List<SaleItemDTO> itemDTOS = sale.getItems().stream()
                .map(item -> SaleItemDTO.builder()
                        .id(item.getId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitprice())
                        .productId(item.getProduct().getId())
                        .build())
                .collect(Collectors.toList());

        return SaleDTO.builder()
                .id(sale.getId())
                .saleDate(sale.getSaleDate())
                .totalValue(sale.getTotalValue())
                .customerId(sale.getCustomer().getId())
                .items(itemDTOS)
                .build();
    }
}