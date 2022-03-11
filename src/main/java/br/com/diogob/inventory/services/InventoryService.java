package br.com.diogob.inventory.services;

import br.com.diogob.inventory.models.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface InventoryService {

    Inventory save(Inventory inventory);

    Optional<Inventory> findById(UUID inventoryId);

    Page<Inventory> findAll(Specification<Inventory> spec, Pageable pageable);

}
