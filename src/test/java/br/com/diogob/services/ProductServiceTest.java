package br.com.diogob.services;

import br.com.diogob.inventory.enums.InventoryType;
import br.com.diogob.inventory.enums.ProductType;
import br.com.diogob.inventory.models.Inventory;
import br.com.diogob.inventory.models.Product;
import br.com.diogob.inventory.repositories.InventoryRepository;
import br.com.diogob.inventory.repositories.ProductRepository;
import br.com.diogob.inventory.services.impl.ProductServiceImpl;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    ProductServiceImpl productService;

    @Mock
    ProductRepository productRepository;

    @Mock
    InventoryRepository inventoryRepository;

    @Mock
    SpecificationTemplate.ProductSpecification spec;

    @Test
    void should_save_product() {
        var product = homeApplianceProduct();

        productService.save(product);

        verify(productRepository, times(1)).save(product);
    }

    @Test
    void should_find_by_id() {
        var homeApplianceProduct = homeApplianceProduct();
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(homeApplianceProduct));

        Optional<Product> optionalProduct = productService.findById(homeApplianceProduct.getProductId());

        assertTrue(optionalProduct.isPresent());
        verify(productRepository, times(1)).findById(homeApplianceProduct.getProductId());
    }

    @Test
    void should_find_all() {
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(homeApplianceProduct(), eletronicProduct(), mobiliaProduct())));

        var pageable = Pageable.ofSize(10);
        Page<Product> products = productService.findAll(spec, pageable);

        assertEquals(3, products.getTotalElements());
        verify(productRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    void should_delete() {
        var product = homeApplianceProduct();
        var inventories = List.of(outInventory());
        when(inventoryRepository.findByProductId(product.getProductId()))
                .thenReturn(inventories);
        doNothing().when(inventoryRepository).deleteAll(inventories);
        doNothing().when(productRepository).delete(product);

        productService.delete(product);

        verify(inventoryRepository, times(1)).deleteAll(inventories);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void should_verify_product_code_use() {
        var product = homeApplianceProduct();
        when(productRepository.findByProductCode(product.getProductCode())).thenReturn(Optional.of(product));

        boolean isInUse = productService.isProductCodeInUse(product.getProductCode());

        assertTrue(isInUse);
        verify(productRepository, times(1)).findByProductCode(product.getProductCode());
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

    private Product eletronicProduct() {
        return Product.builder()
                .productId(UUID.randomUUID())
                .productCode("CCC-111")
                .description("Iphone 11")
                .productType(ProductType.ELETRONICO)
                .supplierValue(new BigDecimal(2500.00))
                .availableAmount(20L)
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

}
