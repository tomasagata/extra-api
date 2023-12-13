package org.mojodojocasahouse.extra.model;
import jakarta.validation.constraints.*;
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
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private ExtraUser user;

    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Pattern(regexp = "^[A-Za-z\\d\\s]+$", message = "Name must only contain letters or numbers")
    @Column(name = "NAME", nullable = false)
    private String name;

    @NotNull(message = "Amount is mandatory")
    @Digits(integer = 12, fraction = 2, message = "Amount must limit to 12 integer places and 2 fraction places")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.01")
    @Column(name="LIMITAMOUNT", nullable = false)
    private BigDecimal limitAmount;

    @NotNull(message = "Amount is mandatory")
    @Digits(integer = 12, fraction = 2, message = "Amount must limit to 12 integer places and 2 fraction places")
    @Column(name="CURRENTAMOUNT", nullable = false)
    private BigDecimal currentAmount;

    @Column(name="LIMITDATE", nullable = false)
    private Date limitDate;

    @Column(name="CREATIONDATE", nullable = false)
    private Date startingDate;

    @NotNull(message = "Category is mandatory")
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z\\d\\s-]+$", message = "Category must only contain letters or numbers")
    @Column(name="CATEGORY", nullable = false)
    private String category;

    @NotNull(message = "IconId is mandatory")
    @Digits(integer = 3, fraction = 0, message = "IconId must limit to 3 integer places")
    @DecimalMin(value = "0", message = "IconId must be greater than 0")
    @DecimalMax(value = "15", message = "IconId must be less than 15")
    @Column(name="ICON_ID", nullable = false)
    private Short iconId;


    public static Budget from(BudgetAddingRequest budgetAddingRequest, ExtraUser user) {
        return new Budget(
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
        public Budget(ExtraUser user, String name, BigDecimal currentAmount, BigDecimal limitAmount, Date limitDate, Date startingDate, String category, Short iconId) {
        this.user = user;
        this.name = name;
        this.limitAmount = limitAmount;
        this.currentAmount = currentAmount;
        this.limitDate = limitDate;
        this.startingDate = startingDate;
        this.category = category;
        this.iconId = iconId;
    }

    public Budget(){}

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