package br.com.diogob.inventory.services.impl;

import br.com.diogob.inventory.models.Inventory;
import br.com.diogob.inventory.models.Product;
import br.com.diogob.inventory.repositories.InventoryRepository;
import br.com.diogob.inventory.repositories.ProductRepository;
import br.com.diogob.inventory.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;

    InventoryRepository inventoryRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Page<Product> findAll(Specification<Product> spec, Pageable pageable) {
        return productRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public void delete(Product product) {
        List<Inventory> inventories = inventoryRepository.findByProductId(product.getProductId());
        inventoryRepository.deleteAll(inventories);
        productRepository.delete(product);
    }

    @Override
    public boolean isProductCodeInUse(String productCode) {
        return productRepository.findByProductCode(productCode).isPresent();
    }

}
