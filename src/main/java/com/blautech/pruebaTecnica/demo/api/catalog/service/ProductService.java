package com.blautech.pruebaTecnica.demo.api.catalog.service;

import com.blautech.pruebaTecnica.demo.api.catalog.model.Product;
import com.blautech.pruebaTecnica.demo.api.catalog.repository.ProductRepository;
import com.blautech.pruebaTecnica.demo.util.StatusConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    // Metodo paginado para obtener todos los productos
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public List<Product> getActiveProducts(){
        return productRepository.findByStatus(StatusConstants.ACTIVE);
    }

    public Product getProductById(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public Product createProduct(Product product){
        if (product.getStatus() == null ||
                (product.getStatus() != StatusConstants.INACTIVE && product.getStatus() != StatusConstants.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Status debe ser " + StatusConstants.INACTIVE + " (desactivado) o " + StatusConstants.ACTIVE + " (activado)");
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails){
        Product existing = getProductById(id);
        existing.setImage(productDetails.getImage());
        existing.setDescription(productDetails.getDescription());
        existing.setAmount(productDetails.getAmount());
        existing.setAvailableQuantity(productDetails.getAvailableQuantity());
        if (productDetails.getStatus() != null &&
                (productDetails.getStatus() == StatusConstants.INACTIVE || productDetails.getStatus() == StatusConstants.ACTIVE)) {
            existing.setStatus(productDetails.getStatus());
        }
        return productRepository.save(existing);
    }

    public void deactivateProduct(Long id){
        Product existing = getProductById(id);
        existing.setStatus(StatusConstants.INACTIVE);
        productRepository.save(existing);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByDescriptionContainingIgnoreCase(query);
    }
}
