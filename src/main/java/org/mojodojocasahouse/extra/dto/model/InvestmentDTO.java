package org.mojodojocasahouse.extra.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class InvestmentDTO {

    private Long id;
    private String name;
    private BigDecimal downPaymentAmount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Timestamp depositStartTimestamp;
    private BigDecimal depositAmount;
    private Integer maxNumberOfDeposits;
    private Integer depositIntervalInDays;
    private CategoryDTO category;

    public InvestmentDTO(Long id,
                         String name,
                         BigDecimal downPaymentAmount,
                         Timestamp depositStartTimestamp,
                         BigDecimal depositAmount,
                         Integer maxNumberOfDeposits,
                         Integer depositIntervalInDays,
                         CategoryDTO category) {
        this.id = id;
        this.name = name;
        this.downPaymentAmount = downPaymentAmount;
        this.depositStartTimestamp = depositStartTimestamp;
        this.depositAmount = depositAmount;
        this.maxNumberOfDeposits = maxNumberOfDeposits;
        this.depositIntervalInDays = depositIntervalInDays;
        this.category = category;
    }

    public InvestmentDTO() {}
}
