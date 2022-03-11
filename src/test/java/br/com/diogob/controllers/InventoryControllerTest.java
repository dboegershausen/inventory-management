package br.com.diogob.controllers;

import br.com.diogob.inventory.controllers.InventoryController;
import br.com.diogob.inventory.controllers.ProductController;
import br.com.diogob.inventory.dtos.InventoryDto;
import br.com.diogob.inventory.dtos.ProductDto;
import br.com.diogob.inventory.enums.InventoryType;
import br.com.diogob.inventory.enums.ProductType;
import br.com.diogob.inventory.models.Inventory;
import br.com.diogob.inventory.models.Product;
import br.com.diogob.inventory.models.ProductProfit;
import br.com.diogob.inventory.services.InventoryService;
import br.com.diogob.inventory.services.ProductAmountService;
import br.com.diogob.inventory.services.ProductProfitService;
import br.com.diogob.inventory.services.ProductService;
import br.com.diogob.inventory.specifications.SpecificationTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @InjectMocks
    InventoryController inventoryController;

    @Mock
    InventoryService inventoryService;

    @Mock
    ProductService productService;

    @Mock
    SpecificationTemplate.InventorySpecification spec;

    @Test
    void should_return_inventory_list() {
        var inventories = List.of(inInventory(), outInventory());
        var page = new PageImpl<>(inventories);
        when(inventoryService.findAll(any(SpecificationTemplate.InventorySpecification.class), any(Pageable.class))).thenReturn(page);

        var inventoriesResponse = inventoryController.getAllInventories(spec, Pageable.ofSize(10));

        assertEquals(2, inventoriesResponse.getBody().getTotalElements());
        assertEquals(HttpStatus.OK, inventoriesResponse.getStatusCode());
        verify(inventoryService, times(1)).findAll(spec, Pageable.ofSize(10));
    }

    @Test
    void should_create_inventory() {
        var inventory = inInventory();
        var inventoryDto = inInventoryDto();
        when(productService.findById(any(UUID.class))).thenReturn(Optional.of(inventory.getProduct()));
        when(inventoryService.save(any(Inventory.class))).thenReturn(inventory);

        var inventoriesResponse = inventoryController.createInventory(inventory.getProduct().getProductId(), inventoryDto);

        inventory.setInventoryId(null);
        assertNotNull(inventoriesResponse.getBody());
        assertEquals(HttpStatus.CREATED, inventoriesResponse.getStatusCode());
        verify(productService, times(1)).findById(inventory.getProduct().getProductId());
        verify(inventoryService, times(1)).save(inventory);
    }

    @Test
    void should_not_create_inventory_for_invalid_product() {
        var productId = UUID.randomUUID();
        var inventoryDto = inInventoryDto();
        when(productService.findById(any(UUID.class))).thenReturn(Optional.empty());

        var inventoriesResponse = inventoryController.createInventory(productId, inventoryDto);

        assertEquals(HttpStatus.NOT_FOUND, inventoriesResponse.getStatusCode());
        verify(productService, times(1)).findById(productId);
    }

    @Test
    void should_validate_product_availability_in_out_inventory() {
        var inventory = outInventory();
        var inventoryDto = outInventoryDto();
        inventoryDto.setInventoryAmount(50L);
        when(productService.findById(any(UUID.class))).thenReturn(Optional.of(inventory.getProduct()));

        var inventoriesResponse = inventoryController.createInventory(inventory.getProduct().getProductId(), inventoryDto);

        assertEquals(HttpStatus.BAD_REQUEST, inventoriesResponse.getStatusCode());
        verify(productService, times(1)).findById(inventory.getProduct().getProductId());
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

    private InventoryDto inInventoryDto() {
        return InventoryDto.builder()
                .inventoryType(InventoryType.ENTRADA)
                .inventoryAmount(10L)
                .build();
    }

    private InventoryDto outInventoryDto() {
        return InventoryDto.builder()
                .inventoryType(InventoryType.SAIDA)
                .inventoryAmount(10L)
                .saleValue(new BigDecimal(5000.00))
                .build();
    }

}
