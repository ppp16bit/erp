package ERP.business.management.repositories;

import ERP.business.management.model.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private  ProductRepository productRepository;

    @Test
    public void shouldSaveProduct() {

        Product product = Product.builder()
                .name("ProductTest")
                .description("desc test")
                .price(100.5f)
                .stockQuantity(1000)
                .barcode("1234567890")
                .build();

        Product savedProduct = productRepository.save(product);

        assertNotNull(savedProduct.getId());
        assertEquals("ProductTest", savedProduct.getName());
        assertEquals("desc test", savedProduct.getDescription());
        assertEquals(100.5f, savedProduct.getPrice());
        assertEquals(1000, savedProduct.getStockQuantity());
        assertEquals("1234567890", savedProduct.getBarcode());
    }

    @Test
    public void shouldFindProductById() {

        Product product = Product.builder()
                .name("product1")
                .description("product1 test")
                .price(200.5f)
                .stockQuantity(40)
                .barcode("1234567890")
                .build();

        Product savedProduct = productRepository.save(product);

        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        assertTrue(foundProduct.isPresent());
        assertEquals(savedProduct.getId(), foundProduct.get().getId());
        assertEquals("product1", foundProduct.get().getName());
    }

    @Test
    public void shouldFindProductByBarcode() {

        String barcode = "UNIQUE12345";
        Product product = Product.builder()
                .name("product2")
                .description("product2 test")
                .price(20.5f)
                .stockQuantity(10)
                .barcode(barcode)
                .build();

        productRepository.save(product);

        Optional<Product> foundProduct = productRepository.findByBarcode(barcode);

        assertTrue(foundProduct.isPresent());
        assertEquals(barcode, foundProduct.get().getBarcode());
        assertEquals("product2", foundProduct.get().getName());
    }

    @Test
    public void shouldUpdateProduct() {

        Product product = Product.builder()
                .name("product3")
                .description("product3 test")
                .price(30.5f)
                .stockQuantity(100)
                .barcode("UPDATE12345")
                .build();

        Product savedProduct = productRepository.save(product);
        UUID productId = savedProduct.getId();

        savedProduct.setName("product3 up");
        savedProduct.setPrice(35.0f);
        Product updateProduct = productRepository.save(savedProduct);

        assertEquals(productId, updateProduct.getId());
        assertEquals("product3 up", updateProduct.getName());
        assertEquals(35.0f, updateProduct.getPrice());
        assertEquals("UPDATE12345", updateProduct.getBarcode());
    }

    @Test
    public void shouldDeleteProduct() {

        Product product = Product.builder()
                .name("product4")
                .description("product4 test")
                .price(50.0f)
                .stockQuantity(2)
                .barcode("DELETE12345")
                .build();

        Product savedProduct = productRepository.save(product);
        UUID productId = savedProduct.getId();

        productRepository.deleteById(productId);
        Optional<Product> deleteProduct = productRepository.findById(productId);

        assertTrue(deleteProduct.isEmpty());
    }

    @Test
    public void shouldFindAllProducts() {

        productRepository.deleteAll();

        Product product1 = Product.builder()
                .name("product1")
                .description("product1 test")
                .price(10.0f)
                .stockQuantity(10)
                .barcode("PROD001")
                .build();

        Product product2 = Product.builder()
                .name("product2")
                .description("poduct2 test")
                .price(20.0f)
                .stockQuantity(20)
                .barcode("PROD002")
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        List<Product> products = productRepository.findAll();

        assertEquals(2, products.size());
        assertTrue(products.stream().anyMatch(product -> product.getBarcode().equals("PROD001")));
        assertTrue(products.stream().anyMatch(product -> product.getBarcode().equals("PROD002")));
    }
}