package ERP.business.management.services;

import ERP.business.management.dto.ProductDTO;
import ERP.business.management.model.product.Product;
import ERP.business.management.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProductDTO> findById(UUID id) {
        return productRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional
    public Optional<ProductDTO> findByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .map(this::convertToDTO);
    }

    @Transactional
    public ProductDTO crate(ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    @Transactional
    public Optional<ProductDTO> update(UUID id, ProductDTO productDTO) {
        if (!productRepository.existsById(id)) {
            return Optional.empty();
        }

        Product product = convertToEntity(productDTO);
        product.setId(id);
        Product updatedProduct = productRepository.save(product);
        return Optional.of(convertToDTO(updatedProduct));
    }

    @Transactional
    public boolean delete(UUID id) {
        if (!productRepository.existsById(id)) {
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }

    @Transactional
    public boolean updateStock(UUID id, int quantity) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            return false;
        }

        Product product = optionalProduct.get();
        int newQuantity = product.getStockQuantity() + quantity;

        if (newQuantity < 0) {
            return  false;
        }

        product.setStockQuantity(newQuantity);
        productRepository.save(product);
        return true;
    }

    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .barcode(product.getBarcode())
                .build();
    }

    private Product convertToEntity(ProductDTO productDTO) {
        return Product.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .stockQuantity(productDTO.getStockQuantity())
                .barcode(productDTO.getBarcode())
                .build();
    }
}
