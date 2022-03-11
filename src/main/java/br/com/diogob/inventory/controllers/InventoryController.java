package br.com.diogob.inventory.controllers;

import br.com.diogob.inventory.dtos.InventoryDto;
import br.com.diogob.inventory.enums.InventoryType;
import br.com.diogob.inventory.models.Inventory;
import br.com.diogob.inventory.models.Product;
import br.com.diogob.inventory.services.InventoryService;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/products/{productId}/inventories")
public class InventoryController {

    InventoryService inventoryService;

    ProductService productService;

    @Autowired
    public InventoryController(InventoryService inventoryService, ProductService productService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Object> createInventory(@PathVariable(value="productId") UUID productId, @RequestBody @Valid InventoryDto inventoryDto) {
        Optional<Product> productOptional = productService.findById(productId);
        if (!productOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (inventoryDto.getInventoryType().equals(InventoryType.SAIDA) && productOptional.get().getAvailableAmount() < inventoryDto.getInventoryAmount()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Product insufficient amount for the operation."));
        }
        var inventory = new Inventory();
        BeanUtils.copyProperties(inventoryDto, inventory);
        inventory.setProduct(productOptional.get());
        if (inventoryDto.getInventoryType().equals(InventoryType.SAIDA)) {
            inventory.setSaleDate(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        }
        inventoryService.save(inventory);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventory);
    }

    @GetMapping
    public ResponseEntity<Page<Inventory>> getAllInventories(SpecificationTemplate.InventorySpecification spec,
                                                       @PageableDefault(page = 0, size = 10, sort = "inventoryId", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(inventoryService.findAll(spec, pageable));
    }

}
