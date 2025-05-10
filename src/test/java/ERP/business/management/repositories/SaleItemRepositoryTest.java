package ERP.business.management.repositories;

import ERP.business.management.model.customer.Customer;
import ERP.business.management.model.customer.CustomerType;
import ERP.business.management.model.product.Product;
import ERP.business.management.model.sale.Sale;
import ERP.business.management.model.sale.SaleItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SaleItemRepositoryTest {

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private Customer testCustomer;
    private Product testProduct1;
    private Product testProduct2;
    private Sale testSale1;
    private Sale testSale2;

    @BeforeEach
    public void setup() {

        testCustomer = Customer.builder()
                .name("customer1")
                .email("customer1@test.com")
                .phone("92000000001")
                .address("Address1, 123")
                .customerType(CustomerType.CPF)
                .build();

        testCustomer = customerRepository.save(testCustomer);

        testProduct1 = Product.builder()
                .name("product1")
                .description("dec 1")
                .price(10.0f)
                .stockQuantity(100)
                .barcode("ITEM12345")
                .build();

        testProduct1 = productRepository.save(testProduct1);

        testProduct2 = Product.builder()
                .name("product2")
                .description("dec 2")
                .price(20.0f)
                .stockQuantity(50)
                .barcode("ITEM67890")
                .build();

        testProduct2 = productRepository.save(testProduct2);

        testSale1 = Sale.builder()
                .saleDate(LocalDate.now())
                .totalValue(100.0f)
                .customer(testCustomer)
                .items(new ArrayList<>())
                .build();

        testSale1 = saleRepository.save(testSale1);

        testSale2 = Sale.builder()
                .saleDate(LocalDate.now().minusDays(1))
                .totalValue(200.0f)
                .customer(testCustomer)
                .items(new ArrayList<>())
                .build();

        testSale2 = saleRepository.save(testSale2);
    }

    @Test
    public void shouldSaveSaleItem() {

        SaleItem saleItem = SaleItem.builder()
                .quantity(5)
                .unitprice(10.0f)
                .sale(testSale1)
                .product(testProduct1)
                .build();

        SaleItem savedSaleItem = saleItemRepository.save(saleItem);

        assertNotNull(savedSaleItem.getId());
        assertEquals(5, savedSaleItem.getQuantity());
        assertEquals(10.0f, savedSaleItem.getUnitprice());
        assertEquals(testSale1.getId(), savedSaleItem.getSale().getId());
        assertEquals(testProduct1.getId(), savedSaleItem.getProduct().getId());
    }

    @Test
    public void shouldFindSaleItemById() {

        SaleItem saleItem = SaleItem.builder()
                .quantity(3)
                .unitprice(20.0f)
                .sale(testSale1)
                .product(testProduct2)
                .build();
        SaleItem savedSaleItem = saleItemRepository.save(saleItem);

        Optional<SaleItem> foundSaleItem = saleItemRepository.findById(savedSaleItem.getId());

        assertTrue(foundSaleItem.isPresent());
        assertEquals(savedSaleItem.getId(), foundSaleItem.get().getId());
        assertEquals(3, foundSaleItem.get().getQuantity());
        assertEquals(20.0f, foundSaleItem.get().getUnitprice());
    }

    @Test
    public void shouldReturnEmptyWhenSaleItemNotFound() {

        Optional<SaleItem> foundSaleItem = saleItemRepository.findById(UUID.randomUUID());

        assertTrue(foundSaleItem.isEmpty());
    }

    @Test
    public void shouldFindSaleItemsBySaleId() {

        SaleItem saleItem1 = SaleItem.builder()
                .quantity(2)
                .unitprice(10.0f)
                .sale(testSale1)
                .product(testProduct1)
                .build();

        saleItemRepository.save(saleItem1);

        SaleItem saleItem2 = SaleItem.builder()
                .quantity(1)
                .unitprice(20.0f)
                .sale(testSale1)
                .product(testProduct2)
                .build();

        saleItemRepository.save(saleItem2);

        SaleItem saleItem3 = SaleItem.builder()
                .quantity(3)
                .unitprice(15.0f)
                .sale(testSale2)
                .product(testProduct1)
                .build();

        saleItemRepository.save(saleItem3);

        List<SaleItem> sale1Items = saleItemRepository.findBySaleId(testSale1.getId());
        List<SaleItem> sale2Items = saleItemRepository.findBySaleId(testSale2.getId());

        assertEquals(2, sale1Items.size());
        assertEquals(1, sale2Items.size());

        assertTrue(sale1Items.stream().allMatch(item -> item.getSale().getId().equals(testSale1.getId())));
        assertTrue(sale2Items.stream().allMatch(item -> item.getSale().getId().equals(testSale2.getId())));
    }

    @Test
    public void shouldReturnEmptyListWhenNoSaleItemsForSale() {

        List<SaleItem> saleItems = saleItemRepository.findBySaleId(testSale1.getId());

        assertTrue(saleItems.isEmpty());
    }

    @Test
    public void shouldDeleteSaleItem() {

        SaleItem saleItem = SaleItem.builder()
                .quantity(4)
                .unitprice(15.0f)
                .sale(testSale1)
                .product(testProduct1)
                .build();
        SaleItem savedSaleItem = saleItemRepository.save(saleItem);
        UUID saleItemId = savedSaleItem.getId();

        saleItemRepository.deleteById(saleItemId);

        Optional<SaleItem> deletedSaleItem = saleItemRepository.findById(saleItemId);
        assertTrue(deletedSaleItem.isEmpty());
    }

    @Test
    public void shouldFindAllSaleItems() {

        saleItemRepository.deleteAll();

        SaleItem saleItem1 = SaleItem.builder()
                .quantity(2)
                .unitprice(10.0f)
                .sale(testSale1)
                .product(testProduct1)
                .build();

        saleItemRepository.save(saleItem1);

        SaleItem saleItem2 = SaleItem.builder()
                .quantity(3)
                .unitprice(15.0f)
                .sale(testSale2)
                .product(testProduct2)
                .build();

        saleItemRepository.save(saleItem2);

        List<SaleItem> allSaleItems = saleItemRepository.findAll();

        assertEquals(2, allSaleItems.size());
    }
}