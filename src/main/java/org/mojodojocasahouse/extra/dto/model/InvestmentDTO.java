package org.mojodojocasahouse.extra.dto.model;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class InvestmentDTO {

    private Long id;
    private String name;
    private BigDecimal downPaymentAmount;
    private Timestamp downPaymentTimestamp;
    private BigDecimal depositAmount;
    private Integer maxNumberOfDeposits;
    private Integer depositIntervalInDays;
    private CategoryDTO category;

    public InvestmentDTO(Long id,
                         String name,
                         BigDecimal downPaymentAmount,
                         Timestamp downPaymentTimestamp,
                         BigDecimal depositAmount,
                         Integer maxNumberOfDeposits,
                         Integer depositIntervalInDays,
                         CategoryDTO category) {
        this.id = id;
        this.name = name;
        this.downPaymentAmount = downPaymentAmount;
        this.downPaymentTimestamp = downPaymentTimestamp;
        this.depositAmount = depositAmount;
        this.maxNumberOfDeposits = maxNumberOfDeposits;
        this.depositIntervalInDays = depositIntervalInDays;
        this.category = category;
    }

    public InvestmentDTO() {}
}
