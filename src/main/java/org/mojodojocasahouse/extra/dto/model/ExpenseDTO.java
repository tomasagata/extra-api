package org.mojodojocasahouse.extra.dto.model;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.Data;

@Data
public class ExpenseDTO {
    private Long id;
    private Long userId;
    private String concept;
    private BigDecimal amount;
    private String date;
    private CategoryDTO category;

    public ExpenseDTO(Long id, Long userId, String concept, BigDecimal amount, Date date, CategoryDTO category) {
        this.id = id;
        this.userId = userId;
        this.concept = concept;
        this.amount = amount;
        this.date = date.toString();
        this.category = category;
    }

}
