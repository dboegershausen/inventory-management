package br.com.diogob.services;

import br.com.diogob.inventory.enums.InventoryType;
import br.com.diogob.inventory.enums.ProductType;
import br.com.diogob.inventory.models.Inventory;
import br.com.diogob.inventory.models.Product;
import br.com.diogob.inventory.repositories.InventoryRepository;
import br.com.diogob.inventory.repositories.ProductRepository;
import br.com.diogob.inventory.services.impl.ProductProfitServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductProfitServiceTest {

    @InjectMocks
    ProductProfitServiceImpl productProfitService;

    @Mock
    ProductRepository productRepository;

    @Mock
    InventoryRepository inventoryRepository;

    @Test
    void should_calculate_product_profit() {
        var product = homeApplianceProduct();
        var inventories = List.of(firstHomeApplianceOutInventory(), secondHomeApplianceOutInventory());
        when(inventoryRepository.findAllOutInventoriesByProductId(product.getProductId())).thenReturn(inventories);

        var productProfit = productProfitService.calculateProductProfit(product);

        assertEquals(15, productProfit.getSalesAmount());
        assertEquals(new BigDecimal(2250.00).setScale(2), productProfit.getProfit());
        verify(inventoryRepository, times(1)).findAllOutInventoriesByProductId(product.getProductId());
    }

    @Test
    void should_calculate_profit() {
        var products = List.of(homeApplianceProduct(), mobiliaProduct());
        var homeApplianceInventories = List.of(firstHomeApplianceOutInventory(), secondHomeApplianceOutInventory());
        var mobiliaInventories = List.of(firstMobiliaOutInventory(), secondMobiliaOutInventory());
        when(productRepository.findAll()).thenReturn(products);
        when(inventoryRepository.findAllOutInventoriesByProductId(products.get(0).getProductId())).thenReturn(homeApplianceInventories);
        when(inventoryRepository.findAllOutInventoriesByProductId(products.get(1).getProductId())).thenReturn(mobiliaInventories);

        var productProfit = productProfitService.calculateProfit();

        assertEquals(15, productProfit.get(0).getSalesAmount());
        assertEquals(8, productProfit.get(1).getSalesAmount());
        assertEquals(new BigDecimal(2250.00).setScale(2), productProfit.get(0).getProfit());
        assertEquals(new BigDecimal(1000.00).setScale(2), productProfit.get(1).getProfit());
        verify(productRepository, times(1)).findAll();
        verify(inventoryRepository, times(1)).findAllOutInventoriesByProductId(products.get(0).getProductId());
        verify(inventoryRepository, times(1)).findAllOutInventoriesByProductId(products.get(1).getProductId());
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

    private Product mobiliaProduct() {
        return Product.builder()
                .productId(UUID.randomUUID())
                .productCode("BBB-111")
                .description("Mesa de Jantar")
                .productType(ProductType.MOVEL)
                .supplierValue(new BigDecimal(500.00))
                .availableAmount(10L)
                .build();
    }

    private Inventory firstHomeApplianceOutInventory() {
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

    private Inventory secondHomeApplianceOutInventory() {
        var product = homeApplianceProduct();
        return Inventory.builder()
                .inventoryId(UUID.randomUUID())
                .product(product)
                .inventoryType(InventoryType.SAIDA)
                .inventoryAmount(5L)
                .saleValue(new BigDecimal(2000.00))
                .saleDate(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();
    }

    private Inventory firstMobiliaOutInventory() {
        var product = mobiliaProduct();
        return Inventory.builder()
                .inventoryId(UUID.randomUUID())
                .product(product)
                .inventoryType(InventoryType.SAIDA)
                .inventoryAmount(5L)
                .saleValue(new BigDecimal(3000.00))
                .saleDate(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();
    }

    private Inventory secondMobiliaOutInventory() {
        var product = mobiliaProduct();
        return Inventory.builder()
                .inventoryId(UUID.randomUUID())
                .product(product)
                .inventoryType(InventoryType.SAIDA)
                .inventoryAmount(3L)
                .saleValue(new BigDecimal(2000.00))
                .saleDate(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();
    }

}
