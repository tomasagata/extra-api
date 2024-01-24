package org.mojodojocasahouse.extra.model;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.mojodojocasahouse.extra.dto.model.BudgetDTO;
import jakarta.persistence.*;
import lombok.Getter;
import org.mojodojocasahouse.extra.dto.requests.BudgetAddingRequest;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(name = "BUDGETS")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Pattern(regexp = "^([A-Za-z\\d]+(\\s[A-Za-z\\d])?)*$",
            message = "Name must only contain letters, numbers or spaces")
    @Column(name = "NAME", nullable = false)
    private String name;

    @NotNull(message = "Amount is mandatory")
    @Digits(integer = 12, fraction = 2, message = "Amount must limit to 12 integer places and 2 fraction places")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.01")
    @Column(name="LIMITAMOUNT", nullable = false)
    private BigDecimal limitAmount;

    @Column(name="LIMITDATE", nullable = false)
    private Date limitDate;

    @Column(name="CREATIONDATE", nullable = false)
    private Date startingDate;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private ExtraUser user;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "linkedBudget", fetch = FetchType.EAGER)
    private Set<Transaction> transactions;


    public static Budget from(BudgetAddingRequest budgetAddingRequest, Category category, ExtraUser user) {
        return new Budget(
                user,
                budgetAddingRequest.getName(),
                budgetAddingRequest.getLimitAmount(),
                budgetAddingRequest.getLimitDate(),
                budgetAddingRequest.getStartingDate(),
                category
        );
    }

    @Valid
    public Budget(ExtraUser user,
                  String name,
                  BigDecimal limitAmount,
                  Date startingDate,
                  Date limitDate,
                  Category category) {
        this.name = name;
        this.limitAmount = limitAmount;
        this.limitDate = limitDate;
        this.startingDate = startingDate;
        this.user = user;
        this.category = category;
        this.transactions = new HashSet<>();
    }

    public Budget(){}

    public BudgetDTO asDto(){
        return new BudgetDTO(
                this.id,
                this.name,
                this.limitAmount,
                this.getCurrentAmount(),
                this.limitDate,
                this.startingDate,
                this.category.asDto()
        );
    }

    public BigDecimal getCurrentAmount() {
        BigDecimal sum = BigDecimal.ZERO;
        for( Transaction transaction: transactions) {
            sum = sum.add(transaction.getAmount());
        }
        return sum;
    }

    public void add(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public void remove(Transaction transaction) {
        this.transactions.remove(transaction);
    }

}