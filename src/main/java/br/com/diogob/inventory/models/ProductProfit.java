package br.com.diogob.inventory.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductProfit {

    private UUID productId;

    private String productCode;

    private Long salesAmount;


    private BigDecimal profit;

}
