package org.mojodojocasahouse.extra.dto;
import java.math.BigDecimal;
import java.sql.Date;
import lombok.Data;

@Data
public class BudgetDTO {
    
    public BudgetDTO(Long id, Long userId, String name, BigDecimal limitAmount, BigDecimal currentAmount, Date limitDate, Date creationDate, String category, Short iconId) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.currentAmount = currentAmount;
        this.limitAmount = limitAmount; 
        this.limitDate = limitDate.toString();
        this.creationDate = creationDate.toString();
        this.category = category;
        this.iconId = iconId;
    }
    private Long id;
    private Long userId;
    private String name;
    private BigDecimal limitAmount;
    private BigDecimal currentAmount;
    private String limitDate;
    private String creationDate;
    private String category;
    private Short iconId;

}
