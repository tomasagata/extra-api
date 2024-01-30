package org.mojodojocasahouse.extra.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

@Data
public class TransactionDTO {

    protected Long id;
    protected String concept;
    protected BigDecimal amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    protected Date date;
    protected CategoryDTO category;
    protected String type;

    public TransactionDTO(Long id, String concept, BigDecimal amount, Date date, CategoryDTO category, String type) {
        this.id = id;
        this.concept = concept;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.type = type;
    }

    public TransactionDTO() {
    }
}
