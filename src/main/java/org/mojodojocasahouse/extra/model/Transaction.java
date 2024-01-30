package org.mojodojocasahouse.extra.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TRANSACTION_TYPE")
public abstract class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotEmpty(message = "Concept cannot be empty")
    @Size(max = 100, message = "Concept cannot exceed 100 characters")
    @Pattern(regexp = "^([A-Za-z\\d]+(\\s[A-Za-z\\d])?)*$",
            message = "Concept must only contain letters, numbers or spaces")
    @Column(name = "CONCEPT", nullable = false)
    protected String concept;

    @NotNull(message = "Amount is mandatory")
    @Digits(integer = 12, fraction = 2, message = "Amount must limit to 12 integer places and 2 fraction places")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.01")
    @Column(name="AMOUNT", nullable = false)
    protected BigDecimal amount;

    @Column(name="DATE", nullable = false)
    protected Date date;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    protected ExtraUser user;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    protected Category category;

    @ManyToOne
    @JoinColumn(name = "BUDGET_ID")
    @Setter
    protected Budget linkedBudget;


}
