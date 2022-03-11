package br.com.diogob.inventory.repositories;


import br.com.diogob.inventory.models.Product;
import br.com.diogob.inventory.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    @Query("SELECT p FROM Product p WHERE p.productType IN (:productTypes)")
    List<Product> findByProductTypeList(@Param("productTypes") List<ProductType> productTypes);

    Optional<Product> findByProductCode(String productCode);

}
