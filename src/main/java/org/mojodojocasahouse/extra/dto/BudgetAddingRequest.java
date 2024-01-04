package org.mojodojocasahouse.extra.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Date;

@Data
public class BudgetAddingRequest {
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Pattern(regexp = "^[A-Za-z\\d\\s]+$", message = "Name must only contain letters or numbers")
    private String name;

    @NotNull(message = "Amount is mandatory")
    @Digits(integer = 12, fraction = 2, message = "Amount must limit to 12 integer places and 2 fraction places")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.01")
    private BigDecimal limitAmount;

    @Future(message = "Date must be in the future")
    private Date limitDate;

    private Date startingDate;

    @NotNull(message = "Category is mandatory")
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z\\d\\s-]+$", message = "Category must only contain letters or numbers")
    private String category;

    @NotNull(message = "IconId is mandatory")
    @Digits(integer = 3, fraction = 0, message = "IconId must limit to 3 integer places")
    @DecimalMin(value = "0", message = "IconId must be greater than 0")
    @DecimalMax(value = "15", message = "IconId must be less than 15")
    private Short iconId;
    
    public BudgetAddingRequest(String name, BigDecimal limitAmount , Date limitDate , Date startingDate, String category, Short iconId) {
        this.name = name;
        this.limitAmount = limitAmount;
        this.limitDate = limitDate;
        this.startingDate = startingDate;
        this.category = category;
        this.iconId = iconId;
    }
    
    public BudgetAddingRequest() {
    }
}
