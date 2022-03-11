package br.com.diogob.services;

import br.com.diogob.inventory.enums.InventoryType;
import br.com.diogob.inventory.enums.ProductType;
import br.com.diogob.inventory.models.Inventory;
import br.com.diogob.inventory.models.Product;
import br.com.diogob.inventory.repositories.InventoryRepository;
import br.com.diogob.inventory.repositories.ProductRepository;
import br.com.diogob.inventory.services.impl.ProductAmountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductAmountServiceTest {

    @InjectMocks
    ProductAmountServiceImpl productAmountService;

    @Mock
    ProductRepository productRepository;

    @Mock
    InventoryRepository inventoryRepository;

    @Test
    void should_calculate_product_amount_for_one_type() {
        var products = List.of(homeApplianceProduct());
        var productTypes = List.of(ProductType.ELETRODOMESTICO);
        var inventories = List.of(firstHomeApplianceOutInventory(), secondHomeApplianceOutInventory());
        when(productRepository.findByProductTypeList(productTypes)).thenReturn(products);
        when(inventoryRepository.findAllOutInventoriesByProductId(products.get(0).getProductId())).thenReturn(inventories);

        var productAmounts = productAmountService.calculateProductAmountByProductTypes(productTypes);

        assertEquals(1, productAmounts.size());
        assertEquals(15, productAmounts.get(0).getAvailableAmount());
        assertEquals(15, productAmounts.get(0).getSaleAmount());
        verify(productRepository, times(1)).findByProductTypeList(productTypes);
        verify(inventoryRepository, times(1)).findAllOutInventoriesByProductId(products.get(0).getProductId());
    }

    @Test
    void should_calculate_product_amount_for_many_types() {
        var products = List.of(homeApplianceProduct(), mobiliaProduct());
        var productTypes = List.of(ProductType.ELETRODOMESTICO, ProductType.MOVEL);
        var homeApplianceInventories = List.of(firstHomeApplianceOutInventory(), secondHomeApplianceOutInventory());
        var mobiliaInventories = List.of(firstMobiliaOutInventory(), secondMobiliaOutInventory());
        when(productRepository.findByProductTypeList(productTypes)).thenReturn(products);
        when(inventoryRepository.findAllOutInventoriesByProductId(products.get(0).getProductId())).thenReturn(homeApplianceInventories);
        when(inventoryRepository.findAllOutInventoriesByProductId(products.get(1).getProductId())).thenReturn(mobiliaInventories);

        var productAmounts = productAmountService.calculateProductAmountByProductTypes(productTypes);

        assertEquals(2, productAmounts.size());
        assertEquals(15, productAmounts.get(0).getAvailableAmount());
        assertEquals(10, productAmounts.get(1).getAvailableAmount());
        assertEquals(15, productAmounts.get(0).getSaleAmount());
        assertEquals(8, productAmounts.get(1).getSaleAmount());
        verify(productRepository, times(1)).findByProductTypeList(productTypes);
        verify(inventoryRepository, times(1)).findAllOutInventoriesByProductId(products.get(0).getProductId());
        verify(inventoryRepository, times(1)).findAllOutInventoriesByProductId(products.get(1).getProductId());
    }

    @Test
    void should_decrement_amount() {
        var product = homeApplianceProduct();
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productAmountService.decrementAmount(product.getProductId(), 10L);
        Optional<Product> optionalProduct = productRepository.findById(product.getProductId());

        assertEquals(5L, optionalProduct.get().getAvailableAmount());
        verify(productRepository, times(2)).findById(product.getProductId());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void should_not_decrement_amount_on_invalid_product() {
        var productId = UUID.randomUUID();
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        productAmountService.decrementAmount(productId, 10L);

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void should_increment_amount() {
        var product = homeApplianceProduct();
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productAmountService.incrementAmount(product.getProductId(), 10L);
        Optional<Product> optionalProduct = productRepository.findById(product.getProductId());

        assertEquals(25L, optionalProduct.get().getAvailableAmount());
        verify(productRepository, times(2)).findById(product.getProductId());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void should_not_increment_amount_on_invalid_product() {
        var productId = UUID.randomUUID();
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        productAmountService.incrementAmount(productId, 10L);

        verify(productRepository, times(1)).findById(productId);
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
