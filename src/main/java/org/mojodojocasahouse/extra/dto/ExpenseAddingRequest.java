package org.mojodojocasahouse.extra.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

@Data
public class ExpenseAddingRequest {
    @Size(max = 100, message = "Concept cannot exceed 100 characters")
    @Pattern(regexp = "^[A-Za-z\\d\\s]+$", message = "Concept must only contain letters or numbers")
    private String concept;

    @NotNull(message = "Amount is mandatory")
    @Digits(integer = 12, fraction = 2, message = "Amount must limit to 12 integer places and 2 fraction places")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.01")
    private BigDecimal amount;

    private Date date;

    @NotNull(message = "Category is mandatory")
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z\\d\\s]+$", message = "Category must only contain letters or numbers")
    private String category;
    
    public ExpenseAddingRequest(String concept, BigDecimal amount, Date date ,String category) {
        this.concept = concept;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }
    
    public ExpenseAddingRequest() {
    }



}
