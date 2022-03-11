package br.com.diogob.inventory.services.impl;

import br.com.diogob.inventory.enums.InventoryType;
import br.com.diogob.inventory.models.Inventory;
import br.com.diogob.inventory.repositories.InventoryRepository;
import br.com.diogob.inventory.services.InventoryService;
import br.com.diogob.inventory.services.ProductAmountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryServiceImpl implements InventoryService {

    InventoryRepository inventoryRepository;

    ProductAmountService productAmountService;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository, ProductAmountService productAmountService) {
        this.inventoryRepository = inventoryRepository;
        this.productAmountService = productAmountService;
    }

    @Override
    @Transactional
    public Inventory save(Inventory inventory) {
        if (inventory.getInventoryType().equals(InventoryType.SAIDA)) {
            productAmountService.decrementAmount(inventory.getProduct().getProductId(), inventory.getInventoryAmount());
        } else {
            productAmountService.incrementAmount(inventory.getProduct().getProductId(), inventory.getInventoryAmount());
        }
        return inventoryRepository.save(inventory);
    }

    @Override
    public Optional<Inventory> findById(UUID inventoryId) {
        return inventoryRepository.findById(inventoryId);
    }

    @Override
    public Page<Inventory> findAll(Specification<Inventory> spec, Pageable pageable) {
        return inventoryRepository.findAll(spec, pageable);
    }

}
