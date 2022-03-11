package br.com.diogob.inventory.services;

import br.com.diogob.inventory.models.Product;
import br.com.diogob.inventory.models.ProductProfit;

import java.util.List;

public interface ProductProfitService {

    ProductProfit calculateProductProfit(Product product);

    List<ProductProfit> calculateProfit();

}
