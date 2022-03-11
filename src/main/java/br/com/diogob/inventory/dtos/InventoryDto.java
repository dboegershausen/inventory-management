package br.com.diogob.inventory.dtos;

import br.com.diogob.inventory.enums.InventoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDto {

    @NotNull(message = "must not be null")
    private InventoryType inventoryType;

    @Min(value = 0, message = "must be positive")
    private BigDecimal saleValue;

    @NotNull(message = "must not be null")
    @Min(value = 0, message = "must be positive")
    private Long inventoryAmount;

}
