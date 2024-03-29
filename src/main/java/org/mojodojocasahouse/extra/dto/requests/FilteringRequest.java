package org.mojodojocasahouse.extra.dto.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.mojodojocasahouse.extra.validation.constraint.ValidDateRange;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@ValidDateRange(
    fromDateField = "from",
    untilDateField = "until"
)
public class FilteringRequest {

    private List<
            @NotNull(message = "Category names in category field cannot be null") // The list itself can be null but not the objects inside.
            @Size(max = 50, message = "Category cannot exceed 50 characters")
            @Pattern(regexp = "^[A-Za-z\\d\\s-]+$", message = "Category must only contain letters or numbers")
            String> categories;

    private Date from;

    private Date until;

    public FilteringRequest() {
        this.categories = new ArrayList<>();
        this.from = null;
        this.until = null;
    }

    public FilteringRequest(@Nullable Date from,
                            @Nullable Date until,
                            @Nullable List<String> categories) {
        this.categories = categories;
        this.from = from;
        this.until = until;
    }
}
