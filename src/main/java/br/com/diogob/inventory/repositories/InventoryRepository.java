package br.com.diogob.inventory.repositories;

import br.com.diogob.inventory.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID>, JpaSpecificationExecutor<Inventory> {

    @Query(value = "SELECT * FROM inventory WHERE product_id = :productId", nativeQuery = true)
    List<Inventory> findByProductId(@Param("productId") UUID productId);

    @Query(value = "SELECT * FROM inventory WHERE product_id = :productId AND inventory_type = 'SAIDA'", nativeQuery = true)
    List<Inventory> findAllOutInventoriesByProductId(@Param("productId") UUID productId);

}
