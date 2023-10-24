package org.mojodojocasahouse.extra.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.mojodojocasahouse.extra.validation.constraint.ValidCategory;

import java.sql.Date;
import java.util.List;

@Data
public class ExpenseFilteringRequest {
    @ValidCategory
    private List<String> categories;

    @PastOrPresent
    private Date from;

    @PastOrPresent
    private Date until;
}
