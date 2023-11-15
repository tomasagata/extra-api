package org.mojodojocasahouse.extra.model;

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
public class ExtraExpense{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private ExtraUser user;

    @Column(name = "CONCEPT", nullable = false)
    private String concept;

    @Column(name="AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name="DATE", nullable = false)
    private Date date;

    @Column(name="CATEGORY", nullable = false)
    private String category;

    @Column(name="ICON_ID", nullable = false)
    private Short iconId;

    public ExtraExpense(){}

    public ExtraExpense(ExtraUser user, String concept, BigDecimal amount, Date date, String category, Short iconId){
        this.user = user;
        this.concept = concept;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.iconId = iconId;
    }

    public static ExtraExpense from(ExpenseAddingRequest expenseAddingRequest, ExtraUser user) {
        return new ExtraExpense(
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