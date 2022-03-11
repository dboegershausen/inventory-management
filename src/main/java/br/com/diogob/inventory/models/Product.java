package br.com.diogob.inventory.models;

import br.com.diogob.inventory.enums.ProductType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID productId;

    @Column(nullable = false, unique = true, length = 50)
    private String productCode;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column(nullable = false)
    private BigDecimal supplierValue;

    @Column(nullable = false)
    private Long availableAmount;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY )
    private Set<Inventory> inventories;

}
