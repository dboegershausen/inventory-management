package br.com.diogob.inventory.services;

import br.com.diogob.inventory.models.ProductAmount;
import br.com.diogob.inventory.enums.ProductType;

import java.util.List;
import java.util.UUID;

public interface ProductAmountService {

    List<ProductAmount> calculateProductAmountByProductTypes(List<ProductType> productTypes);

    void decrementAmount(UUID productId, Long quantity);

    void incrementAmount(UUID productId, Long quantity);

}
