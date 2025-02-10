package com.blautech.pruebaTecnica.demo.api.catalog.repository;

import com.blautech.pruebaTecnica.demo.api.catalog.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Metodo para buscar productos activos
    List<Product> findByStatus(Integer status);

    // Metodo para buscar productos por descripción ignorando mayúsculas/minúsculas
    List<Product> findByDescriptionContainingIgnoreCase(String query);
}
