package org.mojodojocasahouse.extra.dto.model;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExpenseDTO extends TransactionDTO {

    public ExpenseDTO(Long id,
                      String concept,
                      BigDecimal amount,
                      Date date,
                      CategoryDTO category) {
        super(id, concept, amount, date, category, "expense");
    }

    public ExpenseDTO() {}

}
