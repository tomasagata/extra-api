package org.mojodojocasahouse.extra.model;

import jakarta.persistence.Entity;

import org.mojodojocasahouse.extra.dto.ExpenseAddingRequest;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private ExtraUser userId;

    @Column(name = "CONCEPT", nullable = false)
    private String concept;

    @Column(name="AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name="DATE", nullable = false)
    private Date date;

    public ExtraExpense(){}

    public ExtraExpense(ExtraUser user, String concept, BigDecimal amount, Date date){
        this.userId = user;
        this.concept = concept;
        this.amount = amount;
        this.date = date;
    }

    public static ExtraExpense from(ExpenseAddingRequest expenseAddingRequest, ExtraUser user) {
        return new ExtraExpense(
                user,
                expenseAddingRequest.getConcept(),
                expenseAddingRequest.getAmount(),
                expenseAddingRequest.getDate()
        );
    }

}