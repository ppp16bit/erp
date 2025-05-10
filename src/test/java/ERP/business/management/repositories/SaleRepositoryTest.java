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
class SaleRepositoryTest {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private Customer testCustomer;
    private Product testProduct;

    @BeforeEach
    public void saleSetup() {

        testCustomer = Customer.builder()
                .name("sale1")
                .email("sale1@test.com")
                .phone("92000000001")
                .address("address1,111")
                .customerType(CustomerType.CPF)
                .build();

        testCustomer = customerRepository.save(testCustomer);

        testProduct = Product.builder()
                .name("product1")
                .description("desc1 test")
                .price(10.0f)
                .stockQuantity(100)
                .barcode("SALE12345")
                .build();

        testProduct = productRepository.save(testProduct);
    }

    @Test
    public void shouldSaveSale() {

        Sale sale = Sale.builder()
                .saleDate(LocalDate.now())
                .totalValue(100.0f)
                .customer(testCustomer)
                .items(new ArrayList<>())
                .build();

        SaleItem saleItem = SaleItem.builder()
                .quantity(2)
                .unitprice(100.0f)
                .sale(sale)
                .product(testProduct)
                .build();

        sale.getItems().add(saleItem);

        Sale savedSale = saleRepository.save(sale);

        assertNotNull(savedSale.getId());
        assertEquals(LocalDate.now(), savedSale.getSaleDate());
        assertEquals(100.0f,savedSale.getTotalValue());
        assertEquals(testCustomer.getId(), savedSale.getCustomer().getId());
        assertEquals(1, savedSale.getItems().size());
        assertEquals(2, savedSale.getItems().get(0).getQuantity());
    }

    @Test
    public void shouldFindSaleById() {

        Sale sale = Sale.builder()
                .saleDate(LocalDate.now())
                .totalValue(150.0f)
                .customer(testCustomer)
                .items(new ArrayList<>())
                .build();

        SaleItem saleItem = SaleItem.builder()
                .quantity(5)
                .unitprice(100.0f)
                .sale(sale)
                .product(testProduct)
                .build();

        sale.getItems().add(saleItem);
        Sale savedSale = saleRepository.save(sale);

        Optional<Sale> foundSale = saleRepository.findById(savedSale.getId());

        assertTrue(foundSale.isPresent());
        assertEquals(savedSale.getId(), foundSale.get().getId());
        assertEquals(150.0f, foundSale.get().getTotalValue());
    }

    @Test
    public void shouldReturnEmptyWhenSaleNotFound() {

        Optional<Sale> foundSale = saleRepository.findById(UUID.randomUUID());
        assertTrue(foundSale.isEmpty());
    }

    @Test
    public void shouldFindSalesByCustomerId() {

        Customer customer1 = Customer.builder()
                .name("customer1")
                .email("customer1@test.com")
                .phone("11987654321")
                .customerType(CustomerType.CPF)
                .build();
        Customer savedCustomer1 = customerRepository.save(customer1);

        Customer customer2 = Customer.builder()
                .name("customer2")
                .email("customer2@test.com")
                .phone("11987654322")
                .customerType(CustomerType.CNPJ)
                .build();
        Customer savedCustomer2 = customerRepository.save(customer2);

        Sale sale1Customer1 = Sale.builder()
                .saleDate(LocalDate.now())
                .totalValue(100.0f)
                .customer(savedCustomer1)
                .items(new ArrayList<>())
                .build();

        Sale sale2Customer1 = Sale.builder()
                .saleDate(LocalDate.now().minusDays(1))
                .totalValue(200.0f)
                .customer(savedCustomer1)
                .items(new ArrayList<>())
                .build();

        Sale saleCustomer2 = Sale.builder()
                .saleDate(LocalDate.now())
                .totalValue(300.0f)
                .customer(savedCustomer2)
                .items(new ArrayList<>())
                .build();

        saleRepository.save(sale1Customer1);
        saleRepository.save(sale2Customer1);
        saleRepository.save(saleCustomer2);

        List<Sale> customer1Sales = saleRepository.findByCustomerId(savedCustomer1.getId());
        List<Sale> customer2Sales = saleRepository.findByCustomerId(savedCustomer2.getId());

        assertEquals(2, customer1Sales.size());
        assertEquals(1, customer2Sales.size());

        assertTrue(customer1Sales.stream().allMatch(sale -> sale.getCustomer().getId().equals(savedCustomer1.getId())));
        assertTrue(customer2Sales.stream().allMatch(sale -> sale.getCustomer().getId().equals(savedCustomer2.getId())));
    }

    @Test
    public void shouldDeleteSale() {

        Sale sale = Sale.builder()
                .saleDate(LocalDate.now())
                .totalValue(150.0f)
                .customer(testCustomer)
                .items(new ArrayList<>())
                .build();

        Sale savedSale = saleRepository.save(sale);
        UUID saleId = savedSale.getId();

        saleRepository.deleteById(saleId);
        Optional<Sale> deletedSale = saleRepository.findById(saleId);

        assertTrue(deletedSale.isEmpty());
    }

    @Test
    public void shouldFindAllSales() {

        saleRepository.deleteAll();

        Sale sale1 = Sale.builder()
                .saleDate(LocalDate.now())
                .totalValue(100.0f)
                .customer(testCustomer)
                .items(new ArrayList<>())
                .build();

        Sale sale2 = Sale.builder()
                .saleDate(LocalDate.now().minusDays(1))
                .totalValue(200.0f)
                .customer(testCustomer)
                .items(new ArrayList<>())
                .build();

        saleRepository.save(sale1);
        saleRepository.save(sale2);

        List<Sale> sales = saleRepository.findAll();

        assertEquals(2, sales.size());
    }
}