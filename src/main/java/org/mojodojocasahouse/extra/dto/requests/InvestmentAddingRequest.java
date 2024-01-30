package org.mojodojocasahouse.extra.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class InvestmentAddingRequest {

    @NotNull
    private String name;
    @NotNull
    private BigDecimal downPaymentAmount;
    @NotNull
    private Timestamp depositStartTimestamp;
    @NotNull
    private BigDecimal depositAmount;
    @NotNull
    private Integer maxNumberOfDeposits;
    @NotNull
    private Integer depositIntervalInDays;
    @NotNull
    private String category;
    @NotNull
    private Short iconId;

    public InvestmentAddingRequest() {}

    public InvestmentAddingRequest(String name,
                                   BigDecimal downPaymentAmount,
                                   Timestamp depositStartTimestamp,
                                   BigDecimal depositAmount,
                                   Integer maxNumberOfDeposits,
                                   Integer depositIntervalInDays,
                                   String category,
                                   Short iconId) {
        this.name = name;
        this.downPaymentAmount = downPaymentAmount;
        this.depositStartTimestamp = depositStartTimestamp;
        this.depositAmount = depositAmount;
        this.maxNumberOfDeposits = maxNumberOfDeposits;
        this.depositIntervalInDays = depositIntervalInDays;
        this.category = category;
        this.iconId = iconId;
    }

}
