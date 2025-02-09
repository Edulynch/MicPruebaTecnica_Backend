package com.blautech.pruebaTecnica.demo.api.catalog.repository;

import com.blautech.pruebaTecnica.demo.api.catalog.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Retorna productos con el estado dado (por ejemplo, activos)
    List<Product> findByStatus(Integer status);

    // Metodo para buscar productos cuyo campo "description" contenga (ignorando mayúsculas/minúsculas) la cadena de consulta
    List<Product> findByDescriptionContainingIgnoreCase(String query);
}
