package br.com.diogob.inventory.services.impl;

import br.com.diogob.inventory.enums.ProductType;
import br.com.diogob.inventory.models.*;
import br.com.diogob.inventory.repositories.InventoryRepository;
import br.com.diogob.inventory.repositories.ProductRepository;
import br.com.diogob.inventory.services.ProductAmountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductAmountServiceImpl implements ProductAmountService {

    ProductRepository productRepository;

    InventoryRepository inventoryRepository;

    @Autowired
    public ProductAmountServiceImpl(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void decrementAmount(UUID productId, Long quantity) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            var product = optionalProduct.get();
            product.setAvailableAmount(product.getAvailableAmount() - quantity);
            productRepository.save(product);
        }
    }

    @Override
    public void incrementAmount(UUID productId, Long quantity) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            var product = optionalProduct.get();
            product.setAvailableAmount(product.getAvailableAmount() + quantity);
            productRepository.save(product);
        }
    }

    @Override
    public List<ProductAmount> calculateProductAmountByProductTypes(List<ProductType> productTypes) {
        List<ProductAmount> productAmounts = new ArrayList<>();
        List<Product> products = productRepository.findByProductTypeList(productTypes);
        products.stream().forEach(product -> {
            var productAmount = new ProductAmount();
            productAmount.setProductId(product.getProductId());
            productAmount.setProductCode(product.getProductCode());
            productAmount.setAvailableAmount(product.getAvailableAmount());
            productAmount.setSaleAmount(inventoryRepository.findAllOutInventoriesByProductId(product.getProductId()).stream()
                    .map(Inventory::getInventoryAmount).reduce(0L, Long::sum));
            productAmounts.add(productAmount);
        });
        return productAmounts;
    }

}
