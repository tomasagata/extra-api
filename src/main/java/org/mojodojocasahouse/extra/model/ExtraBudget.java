package org.mojodojocasahouse.extra.model;
import org.mojodojocasahouse.extra.dto.BudgetAddingRequest;
import org.mojodojocasahouse.extra.dto.BudgetDTO;
import org.mojodojocasahouse.extra.dto.BudgetEditingRequest;
import jakarta.persistence.*;
import lombok.Getter;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "BUDGETS")
@Getter
public class ExtraBudget{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private ExtraUser user;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name="LIMITAMOUNT", nullable = false)
    private BigDecimal limitAmount;

    @Column(name="CURRENTAMOUNT", nullable = false)
    private BigDecimal currentAmount;

    @Column(name="LIMITDATE", nullable = false)
    private Date limitDate;

    @Column(name="CREATIONDATE", nullable = false)
    private Date startingDate;

    @Column(name="CATEGORY", nullable = false)
    private String category;

    @Column(name="ICON_ID", nullable = false)
    private Short iconId;


    public static ExtraBudget from(BudgetAddingRequest budgetAddingRequest, ExtraUser user) {
        return new ExtraBudget(
                user,
                budgetAddingRequest.getName(),
                BigDecimal.ZERO, //currentAmount SETEADA EN CERO AL CREAR EL PRESUPUESTO
                budgetAddingRequest.getLimitAmount(),
                budgetAddingRequest.getLimitDate(),
                budgetAddingRequest.getStartingDate(),
                budgetAddingRequest.getCategory(),
                budgetAddingRequest.getIconId()
        );
    }
        public ExtraBudget(ExtraUser user, String name, BigDecimal currentAmount, BigDecimal limitAmount, Date limitDate, Date startingDate, String category, Short iconId) {
        this.user = user;
        this.name = name;
        this.limitAmount = limitAmount;
        this.currentAmount = currentAmount;
        this.limitDate = limitDate;
        this.startingDate = startingDate;
        this.category = category;
        this.iconId = iconId;
    }

    public ExtraBudget(){}

    public void updateFrom(BudgetEditingRequest request, ExtraUser user) {
        if (request.getName() != null ) {
            this.name = request.getName();
        }
        if (request.getLimitAmount() != null) {
            this.limitAmount = request.getLimitAmount();
        }
        if (request.getCurrentAmount() != null) {
            this.currentAmount = request.getCurrentAmount();
        }
        if (request.getLimitDate() != null ) {
            this.limitDate = request.getLimitDate();
        }
        if (request.getCreationDate() != null ) {
            this.startingDate = request.getCreationDate();
        }
        if (request.getIconId() != null ) {
            this.iconId = request.getIconId();
        }
        if (request.getCategory() != null ) {
            this.category = request.getCategory();
        }
    }
    public BudgetDTO asDto(){
        return new BudgetDTO(
                this.id,
                this.user.getId(),
                this.name,
                this.limitAmount,
                this.currentAmount,
                this.limitDate,
                this.startingDate,
                this.category,
                this.iconId 
            );
        }
    public void addToCurrentAmount(BigDecimal amountOfExpense) {
        this.currentAmount = this.currentAmount.add(amountOfExpense);
    }
}