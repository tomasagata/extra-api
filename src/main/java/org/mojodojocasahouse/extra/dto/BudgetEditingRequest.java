package org.mojodojocasahouse.extra.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

@Data
public class BudgetEditingRequest {

    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Pattern(regexp = "^[A-Za-z\\d\\s]+$", message = "Name must only contain letters or numbers")
    private String name;

    @Digits(integer = 12, fraction = 2, message = "Limit Amount must limit to 12 integer places and 2 fraction places")
    @DecimalMin(value = "0.01", message = "Limit Amount must be greater than 0.01")
    private BigDecimal limitAmount;
    
    @Digits(integer = 12, fraction = 2, message = "Limit Amount must limit to 12 integer places and 2 fraction places")
    @DecimalMin(value = "0.01", message = "Limit Amount must be greater than 0.01")
    private BigDecimal currentAmount;

    @PastOrPresent(message = "Creation date must be in the past or present")
    private Date creationDate;

    @Future(message = "Limit date must be in the future")
    private Date limitDate;

    @Size(max = 50, message = "Category cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z\\d\\s-]+$", message = "Category must only contain letters or numbers")
    private String category;

    @Digits(integer = 3, fraction = 0, message = "IconId must limit to 3 integer places")
    @DecimalMin(value = "0", message = "IconId must be greater than 0")
    @DecimalMax(value = "15", message = "IconId must be less than 15")
    private Short iconId;
    
    public BudgetEditingRequest(String name, BigDecimal limitAmount,BigDecimal currentAmount, Date creationDate, Date limitDate ,String category, Short iconId) {
        this.name = name;
        this.limitAmount = limitAmount;
        this.currentAmount = currentAmount;
        this.limitDate = limitDate;
        this.creationDate = creationDate;
        this.category = category;
        this.iconId = iconId;
    }
    
    public BudgetEditingRequest() {
    }
}
