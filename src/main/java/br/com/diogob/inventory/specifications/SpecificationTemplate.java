package br.com.diogob.inventory.specifications;

import br.com.diogob.inventory.models.Inventory;
import br.com.diogob.inventory.models.Product;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationTemplate {


    @And({
            @Spec(path = "productType", spec = Equal.class),
            @Spec(path = "productCode", spec = Like.class)
    })
    public interface ProductSpecification extends Specification<Product> {}

    @And({
            @Spec(path = "inventoryType", spec = Equal.class)
    })
    public interface InventorySpecification extends Specification<Inventory> {}

}
