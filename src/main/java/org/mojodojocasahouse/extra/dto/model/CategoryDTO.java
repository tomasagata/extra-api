package org.mojodojocasahouse.extra.dto.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CategoryDTO {

    @NotBlank(message = "Category name is mandatory")
    @Size(max = 50, message = "Category name cannot exceed 50 characters")
    @Pattern(regexp = "^([A-Za-z\\d]+(\\s[A-Za-z\\d])?)*$",
            message = "Category name must only contain letters, numbers or spaces")
    private String name;

    @NotNull(message = "IconId is mandatory")
    @Digits(integer = 3, fraction = 0, message = "IconId must limit to 3 integer places")
    @DecimalMin(value = "0", message = "IconId must be greater than 0")
    @DecimalMax(value = "15", message = "IconId must be less than 15")
    private Short iconId;

    public CategoryDTO(String name, Short iconId) {
        this.name = name;
        this.iconId = iconId;
    }
}
