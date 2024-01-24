package org.mojodojocasahouse.extra.dto.requests;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class InvestmentAddingRequest {

    private String name;
    private BigDecimal downPaymentAmount;
    private Timestamp downPaymentTimestamp;
    private BigDecimal depositAmount;
    private Integer maxNumberOfDeposits;
    private Integer depositIntervalInDays;
    private String category;
    private Short iconId;

    public InvestmentAddingRequest() {}

    public InvestmentAddingRequest(String name,
                                   BigDecimal downPaymentAmount,
                                   Timestamp downPaymentTimestamp,
                                   BigDecimal depositAmount,
                                   Integer maxNumberOfDeposits,
                                   Integer depositIntervalInDays,
                                   String category,
                                   Short iconId) {
        this.name = name;
        this.downPaymentAmount = downPaymentAmount;
        this.downPaymentTimestamp = downPaymentTimestamp;
        this.depositAmount = depositAmount;
        this.maxNumberOfDeposits = maxNumberOfDeposits;
        this.depositIntervalInDays = depositIntervalInDays;
        this.category = category;
        this.iconId = iconId;
    }

}
