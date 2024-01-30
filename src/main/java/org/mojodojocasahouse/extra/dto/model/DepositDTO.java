package org.mojodojocasahouse.extra.dto.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class DepositDTO extends TransactionDTO {

    private Long sourceInvestmentId;

    public DepositDTO(Long id,
                      String concept,
                      BigDecimal amount,
                      Date date,
                      CategoryDTO category,
                      Long sourceInvestmentId) {
        super(id, concept, amount, date, category, "deposit");
        this.sourceInvestmentId = sourceInvestmentId;
    }

    public DepositDTO() {}

}
