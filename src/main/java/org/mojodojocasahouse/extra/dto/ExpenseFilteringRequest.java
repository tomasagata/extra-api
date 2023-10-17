package org.mojodojocasahouse.extra.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ExpenseFilteringRequest {
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z\\d\\s]+$", message = "Category must only contain letters or numbers")
    private String category;
    
}
