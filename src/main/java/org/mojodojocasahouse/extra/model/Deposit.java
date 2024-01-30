package org.mojodojocasahouse.extra.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.mojodojocasahouse.extra.dto.model.DepositDTO;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Getter
@DiscriminatorValue("deposit")
public class Deposit extends Transaction {

    @ManyToOne
    @JoinColumn(name = "SOURCE_INVESTMENT_ID")
    private Investment sourceInvestment;

    public Deposit() {}

    @Valid
    public Deposit(String concept,
                   BigDecimal amount,
                   Date date,
                   ExtraUser user,
                   Category category,
                   Budget linkedBudget,
                   Investment sourceInvestment) {
        this.concept = concept;
        this.amount = amount;
        this.date = date;
        this.user = user;
        this.category = category;
        this.linkedBudget = linkedBudget;
        this.sourceInvestment = sourceInvestment;
    }

    @Override
    public DepositDTO asDto() {
        return new DepositDTO(
                this.id,
                this.concept,
                this.amount,
                this.date,
                this.category.asDto(),
                this.sourceInvestment.getId()
        );
    }

}
