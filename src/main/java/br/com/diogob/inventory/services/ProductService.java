package br.com.diogob.inventory.services;

import br.com.diogob.inventory.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface ProductService {

    Product save(Product product);

    Optional<Product> findById(UUID productId);

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    void delete(Product product);

    boolean isProductCodeInUse(String productCode);

}
