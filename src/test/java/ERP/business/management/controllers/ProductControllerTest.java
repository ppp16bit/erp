package ERP.business.management.controllers;

import ERP.business.management.dto.ProductDTO;
import ERP.business.management.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;

    private UUID productId;
    private ProductDTO productDTO;
    private List<ProductDTO> productList;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        productId = UUID.randomUUID();

        productDTO = new ProductDTO();
        productDTO.setId(productId);
        productDTO.setName("product1");
        productDTO.setDescription("desc 1");
        productDTO.setPrice(10.0f);
        productDTO.setStockQuantity(100);
        productDTO.setBarcode("1234567890123");

        ProductDTO product2 = new ProductDTO();
        product2.setId(UUID.randomUUID());
        product2.setName("product2");
        product2.setDescription("desc 2");
        product2.setPrice(29.99f);
        product2.setStockQuantity(50);
        product2.setBarcode("3210987654321");

        productList = Arrays.asList(productDTO, product2);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() throws Exception {
        when(productService.findAll()).thenReturn(productList);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("product1")))
                .andExpect(jsonPath("$[1].name", is("product2")));

        verify(productService, times(1)).findAll();
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() throws Exception {
        when(productService.findById(productId)).thenReturn(Optional.of(productDTO));

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(productId.toString())))
                .andExpect(jsonPath("$.name", is("product1")))
                .andExpect(jsonPath("$.price", is(10.0)));

        verify(productService, times(1)).findById(productId);
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(productService.findById(productId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).findById(productId);
    }

    @Test
    void getProductByBarCode_WhenProductExists_ShouldReturnProduct() throws Exception {
        String barcode = "1234567890123";
        when(productService.findByBarcode(barcode)).thenReturn(Optional.of(productDTO));

        mockMvc.perform(get("/api/products/barcode/{barcode}", barcode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.barcode", is(barcode)))
                .andExpect(jsonPath("$.name", is("product1")));

        verify(productService, times(1)).findByBarcode(barcode);
    }

    @Test
    void getProductByBarCode_WhenProductDoesNotExist_ShouldReturnNotFound() throws Exception {
        String barcode = "9999999999999";
        when(productService.findByBarcode(barcode)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/barcode/{barcode}", barcode))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).findByBarcode(barcode);
    }

    @Test
    void updateProduct_WhenProductExists_ShouldUpdateStock() throws Exception {
        int quantity = 10;
        when(productService.updateStock(productId, quantity)).thenReturn(true);

        mockMvc.perform(patch("/api/products/{id}", productId)
                        .param("quantity", String.valueOf(quantity))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk());

        verify(productService, times(1)).updateStock(productId, quantity);
    }

    @Test
    void updateProduct_WhenProductDoesNotExist_ShouldReturnNotFound() throws Exception {
        int quantity = 10;
        when(productService.updateStock(productId, quantity)).thenReturn(false);

        mockMvc.perform(patch("/api/products/{id}", productId)
                        .param("quantity", String.valueOf(quantity))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).updateStock(productId, quantity);
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldReturnNoContent() throws Exception {
        when(productService.delete(productId)).thenReturn(true);

        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).delete(productId);
    }

    @Test
    void deleteProduct_WhenProductDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(productService.delete(productId)).thenReturn(false);

        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).delete(productId);
    }
}