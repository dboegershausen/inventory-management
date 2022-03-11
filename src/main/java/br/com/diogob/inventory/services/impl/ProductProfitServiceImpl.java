package br.com.diogob.inventory.services.impl;

import br.com.diogob.inventory.enums.InventoryType;
import br.com.diogob.inventory.models.Inventory;
import br.com.diogob.inventory.models.Product;
import br.com.diogob.inventory.models.ProductProfit;
import br.com.diogob.inventory.repositories.InventoryRepository;
import br.com.diogob.inventory.repositories.ProductRepository;
import br.com.diogob.inventory.services.ProductProfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductProfitServiceImpl implements ProductProfitService {

    ProductRepository productRepository;

    InventoryRepository inventoryRepository;

    @Autowired
    public ProductProfitServiceImpl(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public ProductProfit calculateProductProfit(Product product) {
        List<Inventory> productInventories = inventoryRepository.findAllOutInventoriesByProductId(product.getProductId());
        var productProfit = new ProductProfit();
        productProfit.setProductId(product.getProductId());
        productProfit.setProductCode(product.getProductCode());
        productProfit.setProfit(productInventories.stream().map(inventory ->
                inventory.getSaleValue().subtract(product.getSupplierValue().multiply(new BigDecimal(inventory.getInventoryAmount()))))
                    .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2));
        productProfit.setSalesAmount(productInventories.stream().map(Inventory::getInventoryAmount).reduce(0L, Long::sum));
        return productProfit;
    }

    @Override
    public List<ProductProfit> calculateProfit() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::calculateProductProfit).collect(Collectors.toList());
    }

}
