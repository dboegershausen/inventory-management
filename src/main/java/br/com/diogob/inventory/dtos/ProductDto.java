package br.com.diogob.inventory.dtos;

import br.com.diogob.inventory.enums.ProductType;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ProductDto {

    @NotBlank(message = "must not be blank")
    private String productCode;

    @NotBlank(message = "must not be blank")
    private String description;

    @NotNull(message = "must not be null")
    private ProductType productType;

    @NotNull(message = "must not be null")
    @Min(value = 0, message = "must be positive")
    private BigDecimal supplierValue;

    @NotNull(message = "must not be null")
    @Min(value = 0, message = "must be positive")
    private Long availableAmount;

}
