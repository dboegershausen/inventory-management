package br.com.diogob.inventory.models;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductAmount {

    private UUID productId;

    private String productCode;

    private Long saleAmount;

    private Long availableAmount;

}
