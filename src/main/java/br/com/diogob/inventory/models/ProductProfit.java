package br.com.diogob.inventory.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductProfit {

    private UUID productId;

    private String productCode;

    private Long salesAmount;


    private BigDecimal profit;

}
