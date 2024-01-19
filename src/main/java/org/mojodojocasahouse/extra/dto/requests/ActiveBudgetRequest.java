package org.mojodojocasahouse.extra.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.mojodojocasahouse.extra.dto.model.CategoryDTO;

import java.sql.Date;

@Data
public class ActiveBudgetRequest {

    @NotNull(message = "Category is mandatory")
    private CategoryDTO category;

    private Date date;

    public ActiveBudgetRequest(CategoryDTO category, Date date) {
        this.category = category;
        this.date = date;
    }

    public ActiveBudgetRequest() {}

}
