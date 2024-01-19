package org.mojodojocasahouse.extra.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Setter;

import jakarta.persistence.*;
import lombok.Getter;
import org.mojodojocasahouse.extra.dto.model.ExpenseDTO;
import org.mojodojocasahouse.extra.dto.requests.ExpenseEditingRequest;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Getter
@Table(name = "EXPENSES")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Concept cannot be empty")
    @Size(max = 100, message = "Concept cannot exceed 100 characters")
    @Pattern(regexp = "^([A-Za-z\\d]+(\\s[A-Za-z\\d])?)*$",
            message = "Concept must only contain letters, numbers or spaces")
    @Column(name = "CONCEPT", nullable = false)
    private String concept;

    @NotNull(message = "Amount is mandatory")
    @Digits(integer = 12, fraction = 2, message = "Amount must limit to 12 integer places and 2 fraction places")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.01")
    @Column(name="AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name="DATE", nullable = false)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private ExtraUser user;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "BUDGET_ID", nullable = true)
    @Setter
    private Budget linkedBudget;

    public Expense(){}

    @Valid
    public Expense(ExtraUser user, String concept, BigDecimal amount, Date date, Category category){
        this.user = user;
        this.concept = concept;
        this.amount = amount;
        this.date = date;
        this.category = category;
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

    public ExpenseDTO asDto(){
        return new ExpenseDTO(
                this.id,
                this.user.getId(),
                this.concept,
                this.amount,
                this.date,
                this.category.asDto()
        );
    }
}