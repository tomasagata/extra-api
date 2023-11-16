package org.mojodojocasahouse.extra.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.sql.Date;

@Data
public class ActiveBudgetRequest {

    @NotNull(message = "Category is mandatory")
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z\\d\\s-]+$", message = "Category must only contain letters or numbers")
    private String category;
    private Date date;

    public ActiveBudgetRequest(String category, Date date) {
        this.category = category;
        this.date = date;
    }

    public ActiveBudgetRequest() {}

}
