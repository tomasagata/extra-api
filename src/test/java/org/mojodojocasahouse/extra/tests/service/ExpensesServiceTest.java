package org.mojodojocasahouse.extra.tests.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mojodojocasahouse.extra.dto.ApiResponse;
import org.mojodojocasahouse.extra.dto.ExpenseAddingRequest;
import org.mojodojocasahouse.extra.dto.ExpenseDTO;
import org.mojodojocasahouse.extra.model.ExtraExpense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraExpenseRepository;
import org.mojodojocasahouse.extra.service.ExpenseService;
import org.springframework.boot.test.json.JacksonTester;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ExpensesServiceTest {

    private JacksonTester<List<ExpenseDTO>> jsonExpenseDtoList;

    @Mock
    private ExtraExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }


    @Test
    public void testGettingAllExpensesByExistingUserIdReturnsList() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        ExtraExpense savedExpense1 = new ExtraExpense(user, "Another Concept", new BigDecimal("10.11"), Date.valueOf("2023-09-11"), "test",(short) 1);
        ExtraExpense savedExpense2 = new ExtraExpense(user, "Another Concept", new BigDecimal("10.12"), Date.valueOf("2023-09-12"), "test",(short) 1);

        List<ExtraExpense> expectedExpenses = List.of(
                savedExpense1, savedExpense2
        );

        // Setup - expectations
        given(expenseRepository.findAllExpensesByUser(any())).willReturn(expectedExpenses);

        // exercise
        List<ExpenseDTO> foundExpenseDtos = expenseService.getAllExpensesByUserId(user);

        // verify
        Assertions
                .assertThat(foundExpenseDtos)
                .containsExactlyInAnyOrder(savedExpense1.asDto(), savedExpense2.asDto());
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
        given(expenseRepository.findAllExpensesByUser(any())).willReturn(List.of());

        // exercise
        List<ExpenseDTO> foundExpenses = expenseService.getAllExpensesByUserId(user);

        // verify
        Assertions.assertThat(foundExpenses).isEqualTo(List.of());
    }

    @Test
    public void testAddingAnExpenseToExistingUserReturnsSuccessfulResponse() {
        // Setup - data
        ExpenseAddingRequest request = new ExpenseAddingRequest(
                "A Concept",
                new BigDecimal("10.12"),
                Date.valueOf("2023-09-19"),
                "test",
                (short) 1
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

    @Test
    public void testGettingAllExpensesByCategoryAndUser(){
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        ExtraExpense savedExpense1 = new ExtraExpense(user, "Another Concept", new BigDecimal("10.11"), Date.valueOf("2023-09-11"), "test1",(short) 1);
        ExtraExpense savedExpense2 = new ExtraExpense(user, "Another Concept", new BigDecimal("10.12"), Date.valueOf("2023-09-12"), "test2",(short) 1);
        List<ExpenseDTO> expectedDtos = List.of(savedExpense1.asDto());

        given(expenseRepository.findAllExpensesByUserAndCategory(any(), any())).willReturn(List.of(savedExpense1));

        List<ExpenseDTO> foundExpenses = expenseService.getAllExpensesByCategoryByUserId(user, "test1");

        Assertions.assertThat(foundExpenses).containsExactlyInAnyOrder(expectedDtos.toArray(ExpenseDTO[]::new));
    }

    @Test
    public void testGettingAllDistinctCategoriesOfExpensesOfUser(){
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> expectedCategories = List.of("test1", "test2");

        given(expenseRepository.findAllDistinctCategoriesByUser(any())).willReturn(expectedCategories);

        List<String> foundExpenses = expenseService.getAllCategories(user);

        Assertions.assertThat(foundExpenses).containsExactlyInAnyOrder(expectedCategories.toArray(String[]::new));
    }

}
