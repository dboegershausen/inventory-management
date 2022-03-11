package br.com.diogob.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.com.diogob.inventory.controllers.ProductController;
import br.com.diogob.inventory.dtos.ProductDto;
import br.com.diogob.inventory.enums.ProductType;
import br.com.diogob.inventory.models.Product;
import br.com.diogob.inventory.models.ProductProfit;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @InjectMocks
    ProductController productController;

    @Mock
    ProductService productService;

    @Mock
    ProductAmountService productAmountService;

    @Mock
    ProductProfitService productProfitService;

    @Mock
    SpecificationTemplate.ProductSpecification spec;

    @Test
    void should_return_product_list() {
        var products = List.of(homeApplianceProduct(), mobiliaProduct());
        var page = new PageImpl<>(products);
        when(productService.findAll(any(SpecificationTemplate.ProductSpecification.class), any(Pageable.class))).thenReturn(page);

        var productsResponse = productController.getAllProducts(spec, Pageable.ofSize(10));

        assertEquals(2, productsResponse.getBody().getTotalElements());
        assertEquals(HttpStatus.OK, productsResponse.getStatusCode());
        verify(productService, times(1)).findAll(spec, Pageable.ofSize(10));
    }

    @Test
    void should_return_product() {
        var product = homeApplianceProduct();
        when(productService.findById(any(UUID.class))).thenReturn(Optional.of(product));

        var productsResponse = productController.getProduct(product.getProductId());

        assertNotNull(productsResponse.getBody());
        assertEquals(HttpStatus.OK, productsResponse.getStatusCode());
        verify(productService, times(1)).findById(product.getProductId());
    }

    @Test
    void should_not_return_a_invalid_product() {
        var productId = UUID.randomUUID();
        when(productService.findById(any(UUID.class))).thenReturn(Optional.empty());

        var productsResponse = productController.getProduct(productId);

        assertEquals(HttpStatus.NOT_FOUND, productsResponse.getStatusCode());
        verify(productService, times(1)).findById(productId);
    }

    @Test
    void should_create_product() {
        var product = homeApplianceProduct();
        var productDto = homeApplianceProductDto();
        when(productService.isProductCodeInUse(productDto.getProductCode())).thenReturn(false);
        when(productService.save(any(Product.class))).thenReturn(product);

        var productsResponse = productController.createProduct(productDto);

        product.setProductId(null);
        assertNotNull(productsResponse.getBody());
        assertEquals(HttpStatus.CREATED, productsResponse.getStatusCode());
        verify(productService, times(1)).isProductCodeInUse(productDto.getProductCode());
        verify(productService, times(1)).save(product);
    }

    @Test
    void should_not_create_product_if_code_already_exists() {
        var productDto = homeApplianceProductDto();
        when(productService.isProductCodeInUse(productDto.getProductCode())).thenReturn(true);

        var productsResponse = productController.createProduct(productDto);

        assertNotNull(productsResponse.getBody());
        assertEquals(HttpStatus.CONFLICT, productsResponse.getStatusCode());
        verify(productService, times(1)).isProductCodeInUse(productDto.getProductCode());
    }

    @Test
    void should_update_product_with_same_code() {
        var product = homeApplianceProduct();
        var productDto = homeApplianceProductDto();
        when(productService.findById(any(UUID.class))).thenReturn(Optional.of(product));
        when(productService.save(any(Product.class))).thenReturn(product);

        var productsResponse = productController.updateProduct(product.getProductId(), productDto);

        assertNotNull(productsResponse.getBody());
        assertEquals(HttpStatus.OK, productsResponse.getStatusCode());
        verify(productService, times(1)).findById(product.getProductId());
        verify(productService, times(1)).save(product);
    }

    @Test
    void should_update_product_with_different_code() {
        var product = homeApplianceProduct();
        var productDto = homeApplianceProductDto();
        productDto.setProductCode("AAA-222");
        when(productService.isProductCodeInUse(productDto.getProductCode())).thenReturn(false);
        when(productService.findById(any(UUID.class))).thenReturn(Optional.of(product));
        when(productService.save(any(Product.class))).thenReturn(product);

        var productsResponse = productController.updateProduct(product.getProductId(), productDto);

        assertNotNull(productsResponse.getBody());
        assertEquals(HttpStatus.OK, productsResponse.getStatusCode());
        verify(productService, times(1)).isProductCodeInUse(productDto.getProductCode());
        verify(productService, times(1)).findById(product.getProductId());
        verify(productService, times(1)).save(product);
    }

    @Test
    void should_not_update_product_if_code_already_exists() {
        var product = homeApplianceProduct();
        var productDto = homeApplianceProductDto();
        productDto.setProductCode("AAA-222");
        when(productService.isProductCodeInUse(productDto.getProductCode())).thenReturn(true);
        when(productService.findById(any(UUID.class))).thenReturn(Optional.of(product));

        var productsResponse = productController.updateProduct(product.getProductId(), productDto);

        assertNotNull(productsResponse.getBody());
        assertEquals(HttpStatus.CONFLICT, productsResponse.getStatusCode());
        verify(productService, times(1)).isProductCodeInUse(productDto.getProductCode());
        verify(productService, times(1)).findById(product.getProductId());
    }

    @Test
    void should_not_update_a_invalid_product() {
        var product = homeApplianceProduct();
        var productDto = homeApplianceProductDto();
        when(productService.findById(any(UUID.class))).thenReturn(Optional.empty());

        var productsResponse = productController.updateProduct(product.getProductId(), productDto);

        assertEquals(HttpStatus.NOT_FOUND, productsResponse.getStatusCode());
        verify(productService, times(1)).findById(product.getProductId());
    }

    @Test
    void should_delete_product() {
        var product = homeApplianceProduct();
        when(productService.findById(any(UUID.class))).thenReturn(Optional.of(product));
        doNothing().when(productService).delete(product);

        var productsResponse = productController.deleteProduct(product.getProductId());

        assertEquals(HttpStatus.NO_CONTENT, productsResponse.getStatusCode());
        verify(productService, times(1)).findById(product.getProductId());
        verify(productService, times(1)).delete(product);
    }

    @Test
    void should_not_delete_a_invalid_product() {
        var productId = UUID.randomUUID();
        when(productService.findById(any(UUID.class))).thenReturn(Optional.empty());

        var productsResponse = productController.deleteProduct(productId);

        assertEquals(HttpStatus.NOT_FOUND, productsResponse.getStatusCode());
        verify(productService, times(1)).findById(productId);
    }

    @Test
    void should_calculate_profit() {
        var product = homeApplianceProduct();
        var productProfit = productProfit();
        when(productService.findById(any(UUID.class))).thenReturn(Optional.of(product));
        when(productProfitService.calculateProductProfit(product)).thenReturn(productProfit);

        var productsResponse = productController.getProductProfit(product.getProductId());

        assertNotNull(productsResponse.getBody());
        assertEquals(HttpStatus.OK, productsResponse.getStatusCode());
        verify(productService, times(1)).findById(product.getProductId());
        verify(productProfitService, times(1)).calculateProductProfit(product);
    }

    @Test
    void should_not_calculate_profit_in_a_invalid_product() {
        var productId = UUID.randomUUID();
        when(productService.findById(any(UUID.class))).thenReturn(Optional.empty());

        var productsResponse = productController.getProductProfit(productId);

        assertEquals(HttpStatus.NOT_FOUND, productsResponse.getStatusCode());
        verify(productService, times(1)).findById(productId);
    }

    @Test
    void should_calculate_amount() {
        var productTypes = List.of(ProductType.ELETRODOMESTICO);

        var productsResponse = productController.getProductAmounts(productTypes);

        assertNotNull(productsResponse.getBody());
        assertEquals(HttpStatus.OK, productsResponse.getStatusCode());
        verify(productAmountService, times(1)).calculateProductAmountByProductTypes(productTypes);
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

    private ProductDto homeApplianceProductDto() {
        return ProductDto.builder()
                .productCode("AAA-111")
                .description("Batedeira")
                .productType(ProductType.ELETRODOMESTICO)
                .supplierValue(new BigDecimal(250.00))
                .availableAmount(15L)
                .build();
    }

    private ProductProfit productProfit() {
        return ProductProfit.builder()
                .productCode("AAA-111")
                .productId(UUID.randomUUID())
                .salesAmount(15L)
                .profit(new BigDecimal(2500.00))
                .build();
    }

}
