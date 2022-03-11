package br.com.diogob.inventory.controllers;

import br.com.diogob.inventory.dtos.ProductDto;
import br.com.diogob.inventory.models.Product;
import br.com.diogob.inventory.enums.ProductType;
import br.com.diogob.inventory.services.ProductAmountService;
import br.com.diogob.inventory.services.ProductProfitService;
import br.com.diogob.inventory.services.ProductService;
import br.com.diogob.inventory.specifications.SpecificationTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    ProductService productService;

    ProductProfitService productProfitService;

    ProductAmountService productAmountService;

    @Autowired
    public ProductController(ProductService productService, ProductProfitService productProfitService, ProductAmountService productAmountService) {
        this.productService = productService;
        this.productProfitService = productProfitService;
        this.productAmountService = productAmountService;
    }

    @PostMapping
    public ResponseEntity<Object> createProduct(@RequestBody @Valid ProductDto productDto) {
        if (productService.isProductCodeInUse(productDto.getProductCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","Product code already exists."));
        }
        var product = new Product();
        BeanUtils.copyProperties(productDto, product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(product));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value="productId") UUID productId,
                                               @RequestBody @Valid ProductDto productDto) {
        Optional<Product> productOptional = productService.findById(productId);
        if(!productOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var product = productOptional.get();
        if (!product.getProductCode().equals(productDto.getProductCode()) && productService.isProductCodeInUse(productDto.getProductCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","Product code already exists."));
        }
        BeanUtils.copyProperties(productDto, product);
        return ResponseEntity.status(HttpStatus.OK).body(productService.save(product));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value="productId") UUID productId) {
        Optional<Product> productOptional = productService.findById(productId);
        if(!productOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        productService.delete(productOptional.get());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(SpecificationTemplate.ProductSpecification spec,
                                                       @PageableDefault(page = 0, size = 10, sort = "productId", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findAll(spec, pageable));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Object> getProduct(@PathVariable(value="productId") UUID productId) {
        Optional<Product> productOptional = productService.findById(productId);
        if(!productOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(productOptional.get());
    }

    @GetMapping("/{productId}/profits")
    public ResponseEntity<Object> getProductProfit(@PathVariable(value="productId") UUID productId) {
        Optional<Product> productOptional = productService.findById(productId);

        if(!productOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(productProfitService.calculateProductProfit(productOptional.get()));
    }

    @GetMapping("/amounts")
    public ResponseEntity<Object> getProductAmounts(@RequestParam List<ProductType> productTypes){
        return ResponseEntity.status(HttpStatus.OK).body(productAmountService.calculateProductAmountByProductTypes(productTypes));
    }

}
