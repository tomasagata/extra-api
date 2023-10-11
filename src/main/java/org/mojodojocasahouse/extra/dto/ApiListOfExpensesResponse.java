package org.mojodojocasahouse.extra.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ApiListOfExpensesResponse {

    private List <ExpenseDTO> response;
}