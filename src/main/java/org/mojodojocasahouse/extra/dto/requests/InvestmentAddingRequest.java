package org.mojodojocasahouse.extra.dto.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class InvestmentAddingRequest {

    @NotBlank(message = "Name is mandatory")
    @Size(max = 50, message = "Name cannot exceed 50 characters in length")
    @Pattern(regexp = "^([A-Za-z\\d]+(\\s[A-Za-z\\d])?)*$",
            message = "Category name must only contain letters, numbers or spaces")
    private String name;

    @NotNull(message = "Down payment amount is mandatory")
    @Digits(integer = 12, fraction = 2, message = "Amount must limit to 12 integer places and 2 fraction places")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.01")
    private BigDecimal downPaymentAmount;

    @NotNull(message = "Starting timestamp is mandatory")
    private Timestamp depositStartTimestamp;

    @NotNull(message = "Deposit amount is mandatory")
    @Digits(integer = 12, fraction = 2, message = "Amount must limit to 12 integer places and 2 fraction places")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.01")
    private BigDecimal depositAmount;

    @NotNull(message = "Number of deposits is mandatory")
    @Min(value = 1, message = "At least one deposit is required")
    private Integer maxNumberOfDeposits;

    @NotNull(message = "Deposit interval is mandatory")
    @Min(value = 1, message = "Interval must be of at least 1 day")
    private Integer depositIntervalInDays;

    @NotBlank(message = "Category name is mandatory")
    @Size(max = 50, message = "Category name cannot exceed 50 characters")
    @Pattern(regexp = "^([A-Za-z\\d]+(\\s[A-Za-z\\d])?)*$",
            message = "Category name must only contain letters, numbers or spaces")
    private String category;

    @NotNull(message = "IconId is mandatory")
    @Digits(integer = 3, fraction = 0, message = "IconId must limit to 3 integer places")
    @DecimalMin(value = "0", message = "IconId must be greater than 0")
    @DecimalMax(value = "15", message = "IconId must be less than 15")
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
