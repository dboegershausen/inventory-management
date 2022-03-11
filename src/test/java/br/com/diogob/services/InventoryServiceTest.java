package br.com.diogob.services;

import br.com.diogob.inventory.enums.InventoryType;
import br.com.diogob.inventory.enums.ProductType;
import br.com.diogob.inventory.models.Inventory;
import br.com.diogob.inventory.models.Product;
import br.com.diogob.inventory.repositories.InventoryRepository;
import br.com.diogob.inventory.services.ProductAmountService;
import br.com.diogob.inventory.services.impl.InventoryServiceImpl;
import br.com.diogob.inventory.specifications.SpecificationTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @InjectMocks
    InventoryServiceImpl inventoryService;

    @Mock
    InventoryRepository inventoryRepository;

    @Mock
    ProductAmountService productAmountService;

    @Mock
    SpecificationTemplate.InventorySpecification spec;

    @Test
    void should_save_in_inventory() {
        var inventory = inInventory();
        doNothing().when(productAmountService).incrementAmount(inventory.getProduct().getProductId(), inventory.getInventoryAmount());

        inventoryService.save(inventory);

        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void should_save_out_inventory() {
        var inventory = outInventory();
        doNothing().when(productAmountService).decrementAmount(inventory.getProduct().getProductId(), inventory.getInventoryAmount());

        inventoryService.save(inventory);

        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void should_find_by_id() {
        var inventory = outInventory();
        when(inventoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(inventory));

        Optional<Inventory> optionalInventory = inventoryService.findById(inventory.getInventoryId());

        assertTrue(optionalInventory.isPresent());
        verify(inventoryRepository, times(1)).findById(inventory.getInventoryId());
    }

    @Test
    void should_find_all() {
        when(inventoryRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(inInventory(), outInventory())));

        var pageable = Pageable.ofSize(10);
        Page<Inventory> inventories = inventoryService.findAll(spec, pageable);

        assertEquals(2, inventories.getTotalElements());
        verify(inventoryRepository, times(1)).findAll(spec, pageable);
    }

    private Product homeApplianceProduct() {
        return Product.builder()
                .productId(UUID.randomUUID())
                .productCode("AAA-111")
                .description("Batedeira")
                .productType(ProductType.ELETRODOMESTICO)
                .supplierValue(new BigDecimal(250.00))
                .availableAmount(15L)
                .build();
    }

    private Inventory outInventory() {
        var product = homeApplianceProduct();
        return Inventory.builder()
                .inventoryId(UUID.randomUUID())
                .product(product)
                .inventoryType(InventoryType.SAIDA)
                .inventoryAmount(10L)
                .saleValue(new BigDecimal(4000.00))
                .saleDate(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();
    }

    private Inventory inInventory() {
        var product = homeApplianceProduct();
        return Inventory.builder()
                .inventoryId(UUID.randomUUID())
                .product(product)
                .inventoryType(InventoryType.ENTRADA)
                .inventoryAmount(10L)
                .build();
    }

}
