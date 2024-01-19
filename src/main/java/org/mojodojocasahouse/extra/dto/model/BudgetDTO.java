package org.mojodojocasahouse.extra.dto.model;
import java.math.BigDecimal;
import java.sql.Date;
import lombok.Data;

@Data
public class BudgetDTO {
    
    private Long id;
    private String name;
    private BigDecimal limitAmount;
    private BigDecimal currentAmount;
    private String limitDate;
    private String creationDate;
    private CategoryDTO category;

    public BudgetDTO(Long id, String name, BigDecimal limitAmount, BigDecimal currentAmount, Date limitDate, Date creationDate, CategoryDTO category) {
        this.id = id;
        this.name = name;
        this.currentAmount = currentAmount;
        this.limitAmount = limitAmount;
        this.limitDate = limitDate.toString();
        this.creationDate = creationDate.toString();
        this.category = category;
    }

}
