package org.mojodojocasahouse.extra.dto.model;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

@Data
public class DepositDTO {

    private Long id;
    private String concept;
    private BigDecimal amount;
    private Date date;
    private CategoryDTO category;
    private Long sourceInvestmentId;

    public DepositDTO(Long id,
                      String concept,
                      BigDecimal amount,
                      Date date,
                      CategoryDTO category,
                      Long sourceInvestmentId) {
        this.id = id;
        this.concept = concept;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.sourceInvestmentId = sourceInvestmentId;
    }

    public DepositDTO() {}

}
