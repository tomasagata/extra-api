package org.mojodojocasahouse.extra.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.mojodojocasahouse.extra.dto.model.InvestmentDTO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DOWNPAYMENT_AMOUNT", nullable = false)
    private BigDecimal downPaymentAmount;

    @Column(name = "DOWNPAYMENT_TIMESTAMP", nullable = false)
    private Timestamp downPaymentTimestamp;

    @Column(name = "DEPOSIT_AMOUNT", nullable = false)
    private BigDecimal depositAmount;

    @Column(name = "NUMBER_OF_DEPOSITS", nullable = false)
    private Integer maxNumberOfDeposits;

    @Column(name = "DEPOSIT_INTERVAL_IN_DAYS", nullable = false)
    private Integer depositIntervalInDays;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private ExtraUser user;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "sourceInvestment")
    private Set<Deposit> returns;


    public Investment(String name,
                      BigDecimal downPaymentAmount,
                      Timestamp downPaymentTimestamp,
                      BigDecimal depositAmount,
                      Integer maxNumberOfDeposits,
                      Integer depositIntervalInDays,
                      ExtraUser user,
                      Category category) {
        this.name = name;
        this.downPaymentAmount = downPaymentAmount;
        this.downPaymentTimestamp = downPaymentTimestamp;
        this.depositAmount = depositAmount;
        this.maxNumberOfDeposits = maxNumberOfDeposits;
        this.depositIntervalInDays = depositIntervalInDays;
        this.user = user;
        this.category = category;
        this.returns = new HashSet<>();
    }

    public Investment() {}


    public InvestmentDTO asDto() {
        return new InvestmentDTO(
                id,
                name,
                downPaymentAmount,
                downPaymentTimestamp,
                depositAmount,
                maxNumberOfDeposits,
                depositIntervalInDays,
                category.asDto()
        );
    }
}
