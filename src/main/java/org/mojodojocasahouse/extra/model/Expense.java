package org.mojodojocasahouse.extra.model;

import jakarta.validation.Valid;

import jakarta.persistence.*;
import lombok.Getter;
import org.mojodojocasahouse.extra.dto.model.ExpenseDTO;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Getter
@DiscriminatorValue("expense")
public class Expense extends Transaction {

    public Expense(){}

    @Valid
    public Expense(ExtraUser user, String concept, BigDecimal amount, Date date, Category category){
        this.user = user;
        this.concept = concept;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.signedAmount = amount.negate();
    }

    public void update(String concept, BigDecimal amount, Date date, Category category) {
        if (concept != null ) {
            this.concept = concept;
        }
        if (amount != null) {
            this.amount = amount;
        }
        if (date != null ) {
            this.date = date;
        }
        if (category != null ) {
            this.category = category;
        }
    }

    @Override
    public ExpenseDTO asDto(){
        return new ExpenseDTO(
                this.id,
                this.concept,
                this.amount,
                this.date,
                this.category.asDto()
        );
    }

    @Override
    public BigDecimal getSignedAmount() {
        return this.signedAmount;
    }

}