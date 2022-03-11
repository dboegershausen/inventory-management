package br.com.diogob.inventory.models;

import br.com.diogob.inventory.enums.InventoryType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID inventoryId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InventoryType inventoryType;

    private BigDecimal saleValue;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "dd/MM/yyyy'T'HH:mm:ss'Z'")
    private LocalDateTime saleDate;

    @Column(nullable = false)
    private Long inventoryAmount;

}
