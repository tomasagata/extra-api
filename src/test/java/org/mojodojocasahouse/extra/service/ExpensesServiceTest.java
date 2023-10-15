package org.mojodojocasahouse.extra.service;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mojodojocasahouse.extra.dto.ApiResponse;
import org.mojodojocasahouse.extra.dto.ExpenseAddingRequest;
import org.mojodojocasahouse.extra.model.ExtraExpense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraExpenseRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ExpensesServiceTest {

    @Mock
    private ExtraExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    public void testGettingAllExpensesByExistingUserIdReturnsList() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<ExtraExpense> expectedExpenses = List.of(
                new ExtraExpense(user, "A Concept", new BigDecimal("10.10"), Date.valueOf("2023-09-12")),
                new ExtraExpense(user, "Another Concept", new BigDecimal("10.11"), Date.valueOf("2023-09-11"))
        );

        // Setup - expectations
        given(expenseRepository.findAllExpensesByUserId(any())).willReturn(expectedExpenses);

        // exercise
        List<ExtraExpense> foundExpenses = expenseService.getAllExpensesByUserId(user);

        // verify
        Assertions.assertThat(foundExpenses).isEqualTo(expectedExpenses);
    }

    @Test
    public void testGettingAllExpensesByNonExistingUserIdReturnsEmptyList() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );

        // Setup - expectations
        given(expenseRepository.findAllExpensesByUserId(any())).willReturn(List.of());

        // exercise
        List<ExtraExpense> foundExpenses = expenseService.getAllExpensesByUserId(user);

        // verify
        Assertions.assertThat(foundExpenses).isEqualTo(List.of());
    }

    @Test
    public void testAddingAnExpenseToExistingUserReturnsSuccessfulResponse() {
        // Setup - data
        ExpenseAddingRequest request = new ExpenseAddingRequest(
                "A Concept",
                new BigDecimal("10.12"),
                Date.valueOf("2023-09-19")
        );
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        ApiResponse expectedResponse = new ApiResponse(
                "Expense added succesfully!"
        );

        // exercise
        ApiResponse actualResponse = expenseService.addExpense(user, request);

        // verify
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

}
