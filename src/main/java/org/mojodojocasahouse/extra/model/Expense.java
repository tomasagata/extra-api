package org.mojodojocasahouse.extra.model;

import jakarta.validation.constraints.*;
import org.mojodojocasahouse.extra.dto.ExpenseAddingRequest;

import jakarta.persistence.*;
import lombok.Getter;
import org.mojodojocasahouse.extra.dto.ExpenseDTO;
import org.mojodojocasahouse.extra.dto.ExpenseEditingRequest;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "EXPENSES")
@Getter
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private ExtraUser user;

    @Size(max = 100, message = "Concept cannot exceed 100 characters")
    @Pattern(regexp = "^[A-Za-z\\d\\s]+$", message = "Concept must only contain letters or numbers")
    @Column(name = "CONCEPT", nullable = false)
    private String concept;

    @NotNull(message = "Amount is mandatory")
    @Digits(integer = 12, fraction = 2, message = "Amount must limit to 12 integer places and 2 fraction places")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.01")
    @Column(name="AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name="DATE", nullable = false)
    private Date date;

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

    public Expense(){}

    public Expense(ExtraUser user, String concept, BigDecimal amount, Date date, String category, Short iconId){
        this.user = user;
        this.concept = concept;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.iconId = iconId;
    }

    public static Expense from(ExpenseAddingRequest expenseAddingRequest, ExtraUser user) {
        return new Expense(
                user,
                expenseAddingRequest.getConcept(),
                expenseAddingRequest.getAmount(),
                expenseAddingRequest.getDate(),
                expenseAddingRequest.getCategory(),
                expenseAddingRequest.getIconId()
        );
    }
public void updateFrom(ExpenseEditingRequest request, ExtraUser user) {
    if (request.getConcept() != null ) {
        this.concept = request.getConcept();
    }
    if (request.getAmount() != null) {
        this.amount = request.getAmount();
    }
    if (request.getDate() != null ) {
        this.date = request.getDate();
    }
    if (request.getIconId() != null ) {
        this.iconId = request.getIconId();
    }
    if (request.getCategory() != null ) {
        this.category = request.getCategory();
    }
}

    public ExpenseDTO asDto(){
        return new ExpenseDTO(
                this.id,
                this.user.getId(),
                this.concept,
                this.amount,
                this.date,
                this.category,
                this.iconId
        );
    }

}