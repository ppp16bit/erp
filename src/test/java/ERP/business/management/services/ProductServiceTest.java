package ERP.business.management.services;

import ERP.business.management.dto.ProductDTO;
import ERP.business.management.model.product.Product;
import ERP.business.management.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private UUID productId;
    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        product = Product.builder()
                .id(productId)
                .name("product1")
                .description("desc 1")
                .price(10.0f)
                .stockQuantity(100)
                .barcode("123456789")
                .build();

        productDTO = ProductDTO.builder()
                .id(productId)
                .name("product1")
                .description("desc 1")
                .price(10.0f)
                .stockQuantity(100)
                .barcode("123456789")
                .build();
    }

    @Test
    void findAll_ShouldReturnAllProducts() {

        List<Product> products = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(products);

        List<ProductDTO> result = productService.findAll();

        assertEquals(1, result.size());
        assertEquals(productDTO.getId(), result.get(0).getId());
        assertEquals(productDTO.getName(), result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void findById_WithExistingId_ShouldReturnProduct() {

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Optional<ProductDTO> result = productService.findById(productId);

        assertTrue(result.isPresent());
        assertEquals(productDTO.getId(), result.get().getId());
        assertEquals(productDTO.getName(), result.get().getName());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {

        UUID nonExistingId = UUID.randomUUID();
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Optional<ProductDTO> result = productService.findById(nonExistingId);

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(nonExistingId);
    }

    @Test
    void findByBarcode_WithExistingBarcode_ShouldReturnProduct() {

        String barcode = "123456789";
        when(productRepository.findByBarcode(barcode)).thenReturn(Optional.of(product));

        Optional<ProductDTO> result = productService.findByBarcode(barcode);

        assertTrue(result.isPresent());
        assertEquals(productDTO.getBarcode(), result.get().getBarcode());
        verify(productRepository, times(1)).findByBarcode(barcode);
    }

    @Test
    void findByBarcode_WithNonExistingBarcode_ShouldReturnEmpty() {

        String nonExistingBarcode = "987654321";
        when(productRepository.findByBarcode(nonExistingBarcode)).thenReturn(Optional.empty());

        Optional<ProductDTO> result = productService.findByBarcode(nonExistingBarcode);

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findByBarcode(nonExistingBarcode);
    }

    @Test
    void create_ShouldSaveAndReturnProduct() {

        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO result = productService.crate(productDTO);

        assertNotNull(result);
        assertEquals(productDTO.getName(), result.getName());
        assertEquals(productDTO.getDescription(), result.getDescription());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void update_WithExistingId_ShouldUpdateAndReturnProduct() {

        ProductDTO updatedDTO = ProductDTO.builder()
                .id(productId)
                .name("product1")
                .description("desc 1")
                .price(20.0f)
                .stockQuantity(200)
                .barcode("987654321")
                .build();

        Product updatedProduct = Product.builder()
                .id(productId)
                .name("product2")
                .description("desc 2")
                .price(20.0f)
                .stockQuantity(200)
                .barcode("987654321")
                .build();

        when(productRepository.existsById(productId)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Optional<ProductDTO> result = productService.update(productId, updatedDTO);

        assertTrue(result.isPresent());
        assertEquals("product2", result.get().getName());
        assertEquals("desc 2", result.get().getDescription());
        assertEquals(20.0f, result.get().getPrice());
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void update_WithNonExistingId_ShouldReturnEmpty() {

        UUID nonExistingId = UUID.randomUUID();
        when(productRepository.existsById(nonExistingId)).thenReturn(false);

        Optional<ProductDTO> result = productService.update(nonExistingId, productDTO);

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).existsById(nonExistingId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void delete_WithExistingId_ShouldReturnTrue() {

        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId);

        boolean result = productService.delete(productId);

        assertTrue(result);
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void delete_WithNonExistingId_ShouldReturnFalse() {

        UUID nonExistingId = UUID.randomUUID();
        when(productRepository.existsById(nonExistingId)).thenReturn(false);

        boolean result = productService.delete(nonExistingId);

        assertFalse(result);
        verify(productRepository, times(1)).existsById(nonExistingId);
        verify(productRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void updateStock_WithExistingIdAndSufficientStock_ShouldUpdateStockAndReturnTrue() {

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        boolean result = productService.updateStock(productId, -50);

        assertTrue(result);
        assertEquals(50, product.getStockQuantity());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateStock_WithExistingIdAndInsufficientStock_ShouldReturnFalse() {

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        boolean result = productService.updateStock(productId, -150);

        assertFalse(result);
        assertEquals(100, product.getStockQuantity());  // Stock should remain unchanged
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateStock_WithNonExistingId_ShouldReturnFalse() {

        UUID nonExistingId = UUID.randomUUID();
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        boolean result = productService.updateStock(nonExistingId, 50);

        assertFalse(result);
        verify(productRepository, times(1)).findById(nonExistingId);
        verify(productRepository, never()).save(any(Product.class));
    }
}