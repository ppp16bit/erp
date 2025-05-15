package ERP.business.management.services;

import ERP.business.management.dto.SaleDTO;
import ERP.business.management.dto.SaleItemDTO;
import ERP.business.management.model.customer.Customer;
import ERP.business.management.model.customer.CustomerType;
import ERP.business.management.model.product.Product;
import ERP.business.management.model.sale.Sale;
import ERP.business.management.model.sale.SaleItem;
import ERP.business.management.repositories.CustomerRepository;
import ERP.business.management.repositories.ProductRepository;
import ERP.business.management.repositories.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private SaleService saleService;

    private UUID saleId;
    private UUID customerId;
    private UUID productId;
    private Customer customer;
    private Product product;
    private Sale sale;
    private SaleDTO saleDTO;
    private SaleItem saleItem;
    private SaleItemDTO saleItemDTO;

    @BeforeEach
    void setUp() {
        saleId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        productId = UUID.randomUUID();

        customer = Customer.builder()
                .id(customerId)
                .name("customer1")
                .email("customer1@test.com")
                .customerType(CustomerType.CPF)
                .build();

        product = Product.builder()
                .id(productId)
                .name("product1")
                .price(10.0f)
                .stockQuantity(100)
                .build();

        saleItemDTO = SaleItemDTO.builder()
                .productId(productId)
                .quantity(2)
                .unitPrice(10.0f)
                .build();

        List<SaleItemDTO> saleItemDTOs = new ArrayList<>();
        saleItemDTOs.add(saleItemDTO);

        saleDTO = SaleDTO.builder()
                .id(saleId)
                .customerId(customerId)
                .saleDate(LocalDate.now())
                .items(saleItemDTOs)
                .totalValue(20.0f)
                .build();

        saleItem = SaleItem.builder()
                .id(UUID.randomUUID())
                .product(product)
                .quantity(2)
                .unitprice(10.0f)
                .build();

        List<SaleItem> saleItems = new ArrayList<>();
        saleItems.add(saleItem);

        sale = Sale.builder()
                .id(saleId)
                .customer(customer)
                .saleDate(LocalDate.now())
                .items(saleItems)
                .totalValue(20.0f)
                .build();

        saleItem.setSale(sale);
    }

    @Test
    void findAll_ShouldReturnAllSales() {

        List<Sale> sales = Arrays.asList(sale);
        when(saleRepository.findAll()).thenReturn(sales);

        List<SaleDTO> result = saleService.findAll();

        assertEquals(1, result.size());
        assertEquals(saleDTO.getId(), result.get(0).getId());
        assertEquals(saleDTO.getCustomerId(), result.get(0).getCustomerId());
        assertEquals(saleDTO.getTotalValue(), result.get(0).getTotalValue());
        verify(saleRepository, times(1)).findAll();
    }

    @Test
    void findById_WithExistingId_ShouldReturnSale() {

        when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));

        Optional<SaleDTO> result = saleService.findById(saleId);

        assertTrue(result.isPresent());
        assertEquals(saleDTO.getId(), result.get().getId());
        assertEquals(saleDTO.getCustomerId(), result.get().getCustomerId());
        assertEquals(saleDTO.getTotalValue(), result.get().getTotalValue());
        verify(saleRepository, times(1)).findById(saleId);
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {

        UUID nonExistingId = UUID.randomUUID();
        when(saleRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Optional<SaleDTO> result = saleService.findById(nonExistingId);

        assertFalse(result.isPresent());
        verify(saleRepository, times(1)).findById(nonExistingId);
    }

    @Test
    void findByCustomerId_ShouldReturnCustomerSales() {

        List<Sale> sales = Arrays.asList(sale);
        when(saleRepository.findByCustomerId(customerId)).thenReturn(sales);

        List<SaleDTO> result = saleService.findByCustomerId(customerId);

        assertEquals(1, result.size());
        assertEquals(customerId, result.get(0).getCustomerId());
        verify(saleRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    void create_WithValidData_ShouldCreateSale() {

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productService.updateStock(productId, -2)).thenReturn(true);
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);

        Optional<SaleDTO> result = saleService.create(saleDTO);

        assertTrue(result.isPresent());
        assertEquals(saleDTO.getCustomerId(), result.get().getCustomerId());
        assertEquals(saleDTO.getTotalValue(), result.get().getTotalValue());
        assertEquals(1, result.get().getItems().size());
        verify(customerRepository, times(1)).findById(customerId);
        verify(productRepository, times(1)).findById(productId);
        verify(productService, times(1)).updateStock(productId, -2);
        verify(saleRepository, times(1)).save(any(Sale.class));
    }

    @Test
    void create_WithNonExistingCustomer_ShouldReturnEmpty() {

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        Optional<SaleDTO> result = saleService.create(saleDTO);

        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById(customerId);
        verify(productRepository, never()).findById(any(UUID.class));
        verify(productService, never()).updateStock(any(UUID.class), anyInt());
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    void create_WithNonExistingProduct_ShouldReturnEmpty() {

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Optional<SaleDTO> result = saleService.create(saleDTO);

        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById(customerId);
        verify(productRepository, times(1)).findById(productId);
        verify(productService, never()).updateStock(any(UUID.class), anyInt());
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    void create_WithInsufficientStock_ShouldReturnEmpty() {

        product.setStockQuantity(1); // Set stock to less than required quantity
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Optional<SaleDTO> result = saleService.create(saleDTO);

        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById(customerId);
        verify(productRepository, times(1)).findById(productId);
        verify(productService, never()).updateStock(any(UUID.class), anyInt());
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    void delete_WithExistingId_ShouldDeleteSaleAndReturnTrue() {

        when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
        when(productService.updateStock(productId, 2)).thenReturn(true);
        doNothing().when(saleRepository).deleteById(saleId);

        boolean result = saleService.delete(saleId);

        assertTrue(result);
        verify(saleRepository, times(1)).findById(saleId);
        verify(productService, times(1)).updateStock(productId, 2);
        verify(saleRepository, times(1)).deleteById(saleId);
    }

    @Test
    void delete_WithNonExistingId_ShouldReturnFalse() {

        UUID nonExistingId = UUID.randomUUID();
        when(saleRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        boolean result = saleService.delete(nonExistingId);

        assertFalse(result);
        verify(saleRepository, times(1)).findById(nonExistingId);
        verify(productService, never()).updateStock(any(UUID.class), anyInt());
        verify(saleRepository, never()).deleteById(any(UUID.class));
    }
}