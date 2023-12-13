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
import org.mojodojocasahouse.extra.dto.ExpenseEditingRequest;
import org.mojodojocasahouse.extra.model.Expense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExpenseRepository;
import org.mojodojocasahouse.extra.service.BudgetService;
import org.mojodojocasahouse.extra.service.ExpenseService;
import org.springframework.boot.test.json.JacksonTester;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpensesServiceTest {


    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Mock
    private BudgetService budgetService;

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
        Expense savedExpense1 = new Expense(user, "Another Concept", new BigDecimal("10.11"), Date.valueOf("2023-09-11"), "test",(short) 1);
        Expense savedExpense2 = new Expense(user, "Another Concept", new BigDecimal("10.12"), Date.valueOf("2023-09-12"), "test",(short) 1);

        List<Expense> expectedExpenses = List.of(
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
                "Expense added successfully!"
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
        Expense savedExpense1 = new Expense(user, "Another Concept", new BigDecimal("10.11"), Date.valueOf("2023-09-11"), "test1",(short) 1);
        new Expense(user, "Another Concept", new BigDecimal("10.12"), Date.valueOf("2023-09-12"), "test2",(short) 1);
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

    @Test
    public void testExpenseCanBeEdited(){
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        Expense savedExpense1 = new Expense(user,"Another Concept", new BigDecimal("10.11"), Date.valueOf("2023-09-11"), "test1",(short) 1);
        given(expenseRepository.findById(any())).willReturn(java.util.Optional.of(savedExpense1));
        Long id = (long) 0;
        ExpenseEditingRequest request = new ExpenseEditingRequest(
                "Anoother concept",
                null,
                null,
                null,
                null
        );
        ApiResponse expectedResponse = new ApiResponse(
                "Expense edited successfully!"
        );
        ApiResponse actualResponse = expenseService.editExpense(user,id, request);
        
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }  

    @Test
    public void testExpenseCanBeDeleted(){
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        Expense savedExpense1 = new Expense(user,"Another Concept", new BigDecimal("10.11"), Date.valueOf("2023-09-11"), "test1",(short) 1);
        expenseRepository.save(savedExpense1);
        Long id = (long) 0;
        expenseService.deleteById(id);
        Assertions.assertThat(expenseService.existsById(id)).isEqualTo(false);
    }
    @Test
    public void testExpenseHaveOwner(){
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        Expense savedExpense1 = new Expense(user,"Another Concept", new BigDecimal("10.11"), Date.valueOf("2023-09-11"), "test1",(short) 1);
        expenseRepository.save(savedExpense1);
        Long id = (long) 0;
        Assertions.assertThat(expenseService.isOwner(user, id)).isEqualTo(false);
    }

    @Test
    public void testGettingSumOfExpenses_WithoutParameters_ReturnsSumOfAllExpenses() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of();
        Date fromParameter = null;
        Date untilParameter = null;

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("category", "cat1", "amount", "100"),
                Map.of("category", "cat2", "amount", "200"),
                Map.of("category", "cat3", "amount", "300")
        );

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getSumOfExpensesByCategories(any(ExtraUser.class), any()))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getSumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }

    @Test
    public void testGettingSumOfExpenses_WithNoFromDateButWithUntilDateAndWithCategoryParameters_ReturnsSumOfAllCategoryExpensesUntilGivenDate() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of("cat1", "cat2");
        Date fromParameter = null;
        Date untilParameter = Date.valueOf("2022-10-10");

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("category", "cat1", "amount", "100"),
                Map.of("category", "cat2", "amount", "200")
        );

        // Setup - expectations
        given(expenseRepository.getSumOfExpensesOfUserBeforeGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getSumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(0)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }

    @Test
    public void testGettingSumOfExpenses_WithAllParameters_ReturnsSumOfAllCategoryExpensesWithinThoseRangesAndCategories() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of("cat1", "cat2");
        Date fromParameter = Date.valueOf("2020-10-10");
        Date untilParameter = Date.valueOf("2022-10-10");

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("category", "cat2", "amount", "200")
        );

        // Setup - expectations
        given(expenseRepository.getSumOfExpensesOfUserByCategoryAndDateInterval(any(ExtraUser.class), any(), any(Date.class), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getSumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(0)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }

    @Test
    public void testGettingSumOfExpenses_WithNoUntilDateButWithFromDateAndWithCategoryParameters_ReturnsSumOfAllCategoryExpensesFromGivenDate() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of("cat1", "cat2");
        Date fromParameter = Date.valueOf("2020-10-10");
        Date untilParameter = null;

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("category", "cat2", "amount", "200")
        );

        // Setup - expectations
        given(expenseRepository.getSumOfExpensesOfUserAfterGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getSumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, never()).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }

    @Test
    public void testGettingSumOfExpenses_WithNoFromDateNoUntilDateButWithCategoryParameters_ReturnsSumOfAllCategoryExpenses() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of("cat1", "cat2");
        Date fromParameter = null;
        Date untilParameter = null;

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("category", "cat1", "amount", "100"),
                Map.of("category", "cat2", "amount", "200")
        );

        // Setup - expectations
        given(expenseRepository.getSumOfExpensesByCategories(any(ExtraUser.class), any()))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getSumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, never()).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }

    @Test
    public void testGettingSumOfExpenses_WithNoFromDateNoCategoryParametersButWithUntilDate_ReturnsSumOfAllExpensesUntilGivenDate() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of();
        Date fromParameter = null;
        Date untilParameter = Date.valueOf("2022-10-10");

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("category", "cat1", "amount", "100"),
                Map.of("category", "cat2", "amount", "200")
        );

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getSumOfExpensesOfUserBeforeGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getSumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(1)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }

    @Test
    public void testGettingSumOfExpenses_WithDateRangesButNoCategories_ReturnsSumOfAllExpensesWithinThoseRanges() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of();
        Date fromParameter = Date.valueOf("2020-10-10");
        Date untilParameter = Date.valueOf("2022-10-10");

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("category", "cat2", "amount", "200")
        );

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getSumOfExpensesOfUserByCategoryAndDateInterval(any(ExtraUser.class), any(), any(Date.class), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getSumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(1)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }

    @Test
    public void testGettingSumOfExpenses_WithNoUntilDateNoCategoryParametersButWithFromDate_ReturnsSumOfAllExpensesFromGivenDate() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of();
        Date fromParameter = Date.valueOf("2020-10-10");
        Date untilParameter = null;

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("category", "cat2", "amount", "200"),
                Map.of("category", "cat3", "amount", "300")
        );

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getSumOfExpensesOfUserAfterGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getSumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(1)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }






    @Test
    public void testGettingExpenses_WithoutParameters_ReturnsSumOfAllExpenses() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of();
        Date fromParameter = null;
        Date untilParameter = null;

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 1", new BigDecimal("100.0"), Date.valueOf("2020-09-12"), "cat1",(short) 1),
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), "cat2",(short) 2),
                new Expense(user, "Expense 3", new BigDecimal("300.0"), Date.valueOf("2023-09-12"), "cat3",(short) 3)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getExpensesOfUserByCategory(any(ExtraUser.class), any()))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(1)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }

    @Test
    public void testGettingExpenses_WithNoFromDateButWithUntilDateAndWithCategoryParameters_ReturnsSumOfAllCategoryExpensesUntilGivenDate() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of("cat1", "cat2");
        Date fromParameter = null;
        Date untilParameter = Date.valueOf("2022-10-10");

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 1", new BigDecimal("100.0"), Date.valueOf("2020-09-12"), "cat1",(short) 1),
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), "cat2",(short) 2)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(expenseRepository.getExpensesOfUserBeforeGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(0)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }

    @Test
    public void testGettingExpenses_WithAllParameters_ReturnsSumOfAllCategoryExpensesWithinThoseRangesAndCategories() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of("cat1", "cat2");
        Date fromParameter = Date.valueOf("2020-10-10");
        Date untilParameter = Date.valueOf("2022-10-10");

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), "cat2",(short) 2)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(expenseRepository.getExpensesOfUserByCategoriesAndDateInterval(any(ExtraUser.class), any(), any(Date.class), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(0)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }

    @Test
    public void testGettingExpenses_WithNoUntilDateButWithFromDateAndWithCategoryParameters_ReturnsSumOfAllCategoryExpensesFromGivenDate() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of("cat1", "cat2");
        Date fromParameter = Date.valueOf("2020-10-10");
        Date untilParameter = null;

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), "cat2",(short) 2)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(expenseRepository.getExpensesOfUserAfterGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(0)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }

    @Test
    public void testGettingExpenses_WithNoFromDateNoUntilDateButWithCategoryParameters_ReturnsSumOfAllCategoryExpenses() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of("cat1", "cat2");
        Date fromParameter = null;
        Date untilParameter = null;

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 1", new BigDecimal("100.0"), Date.valueOf("2020-09-12"), "cat1",(short) 1),
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), "cat2",(short) 2)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(expenseRepository.getExpensesOfUserByCategory(any(ExtraUser.class), any()))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(0)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }

    @Test
    public void testGettingExpenses_WithNoFromDateNoCategoryParametersButWithUntilDate_ReturnsSumOfAllExpensesUntilGivenDate() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of();
        Date fromParameter = null;
        Date untilParameter = Date.valueOf("2022-10-10");

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 1", new BigDecimal("100.0"), Date.valueOf("2020-09-12"), "cat1",(short) 1),
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), "cat2",(short) 2)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getExpensesOfUserBeforeGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(1)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }

    @Test
    public void testGettingExpenses_WithDateRangesButNoCategories_ReturnsSumOfAllExpensesWithinThoseRanges() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of();
        Date fromParameter = Date.valueOf("2020-10-10");
        Date untilParameter = Date.valueOf("2022-10-10");

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), "cat2",(short) 2)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getExpensesOfUserByCategoriesAndDateInterval(any(ExtraUser.class), any(), any(Date.class), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(1)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }

    @Test
    public void testGettingExpenses_WithNoUntilDateNoCategoryParametersButWithFromDate_ReturnsSumOfAllExpensesFromGivenDate() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of();
        Date fromParameter = Date.valueOf("2020-10-10");
        Date untilParameter = null;

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), "cat2",(short) 2),
                new Expense(user, "Expense 3", new BigDecimal("300.0"), Date.valueOf("2023-09-12"), "cat3",(short) 3)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getExpensesOfUserAfterGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(1)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }

    @Test
    public void testGettingAllCategoriesAlongWithIcons_WithDistinctCategoriesInBothBudgetsAndExpenses_ReturnsJsonObjectWithCategoryAndIconIdAttributes() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<Map<String, String>> existingBudgetCategories = List.of(
                Map.of("category", "cat1", "iconId", "1"),
                Map.of("category", "cat2", "iconId", "2"),
                Map.of("category", "cat3", "iconId", "3")
        );
        List<Map<String, String>> existingExpenseCategories = List.of(
                Map.of("category", "cat1", "iconId", "3"),
                Map.of("category", "cat2", "iconId", "2"),
                Map.of("category", "cat3", "iconId", "1")
        );
        List<Map<String, String>> expectedResults = List.of(
                Map.of("category", "cat1", "iconId", "1"),
                Map.of("category", "cat2", "iconId", "2"),
                Map.of("category", "cat3", "iconId", "3"),
                // Map.of("category", "cat2", "iconId", "2"), is repeated
                Map.of("category", "cat1", "iconId", "3"),
                Map.of("category", "cat3", "iconId", "1")
        );

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUserWithIcons(any(ExtraUser.class)))
                .willReturn(existingExpenseCategories);
        given(budgetService.getAllCategoriesWithIcons(any(ExtraUser.class)))
                .willReturn(existingBudgetCategories);

        // Execute
        List<Map<String, String>> results = expenseService.getAllCategoriesWithIcons(user);

        // Verify
        Assertions.assertThat(results.toArray()).containsExactlyInAnyOrder(expectedResults.toArray());
    }

    @Test
    public void testGettingAllCategoriesAlongWithIcons_WithDistinctCategoriesInOnlyBudgets_ReturnsJsonObjectWithCategoryAndIconIdAttributes() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<Map<String, String>> existingBudgetCategories = List.of(
                Map.of("category", "cat1", "iconId", "1"),
                Map.of("category", "cat2", "iconId", "2"),
                Map.of("category", "cat3", "iconId", "3")
        );
        List<Map<String, String>> existingExpenseCategories = List.of();
        List<Map<String, String>> expectedResults = List.of(
                Map.of("category", "cat1", "iconId", "1"),
                Map.of("category", "cat2", "iconId", "2"),
                Map.of("category", "cat3", "iconId", "3")
        );

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUserWithIcons(any(ExtraUser.class)))
                .willReturn(existingExpenseCategories);
        given(budgetService.getAllCategoriesWithIcons(any(ExtraUser.class)))
                .willReturn(existingBudgetCategories);

        // Execute
        List<Map<String, String>> results = expenseService.getAllCategoriesWithIcons(user);

        // Verify
        Assertions.assertThat(results.toArray()).containsExactlyInAnyOrder(expectedResults.toArray());

    }

    @Test
    public void testGettingAllCategoriesAlongWithIcons_WithDistinctCategoriesInOnlyExpenses_ReturnsJsonObjectWithCategoryAndIconIdAttributes() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<Map<String, String>> existingBudgetCategories = List.of();
        List<Map<String, String>> existingExpenseCategories = List.of(
                Map.of("category", "cat1", "iconId", "3"),
                Map.of("category", "cat2", "iconId", "2"),
                Map.of("category", "cat3", "iconId", "1")
        );
        List<Map<String, String>> expectedResults = List.of(
                Map.of("category", "cat1", "iconId", "3"),
                Map.of("category", "cat2", "iconId", "2"),
                Map.of("category", "cat3", "iconId", "1")
        );

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUserWithIcons(any(ExtraUser.class)))
                .willReturn(existingExpenseCategories);
        given(budgetService.getAllCategoriesWithIcons(any(ExtraUser.class)))
                .willReturn(existingBudgetCategories);

        // Execute
        List<Map<String, String>> results = expenseService.getAllCategoriesWithIcons(user);

        // Verify
        Assertions.assertThat(results.toArray()).containsExactlyInAnyOrder(expectedResults.toArray());

    }

    @Test
    public void testGettingAllCategoriesAlongWithIcons_WithDistinctCategoriesInNeither_ReturnsJsonObjectWithCategoryAndIconIdAttributes() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<Map<String, String>> existingBudgetCategories = List.of();
        List<Map<String, String>> existingExpenseCategories = List.of();
        List<Map<String, String>> expectedResults = List.of();

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUserWithIcons(any(ExtraUser.class)))
                .willReturn(existingExpenseCategories);
        given(budgetService.getAllCategoriesWithIcons(any(ExtraUser.class)))
                .willReturn(existingBudgetCategories);

        // Execute
        List<Map<String, String>> results = expenseService.getAllCategoriesWithIcons(user);

        // Verify
        Assertions.assertThat(results.toArray()).containsExactlyInAnyOrder(expectedResults.toArray());

    }







    @Test
    public void testGettingYearlySumOfExpenses_WithoutParameters_ReturnsSumOfAllExpenses() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of();
        Date fromParameter = null;
        Date untilParameter = null;

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("year", "2020", "amount", "100"),
                Map.of("year", "2021", "amount", "200"),
                Map.of("year", "2022", "amount", "300")
        );

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getYearlySumOfExpensesByCategories(any(ExtraUser.class), any()))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getYearlySumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }

    @Test
    public void testGettingYearlySumOfExpenses_WithNoFromDateButWithUntilDateAndWithCategoryParameters_ReturnsSumOfAllCategoryExpensesUntilGivenDate() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of("cat1", "cat2");
        Date fromParameter = null;
        Date untilParameter = Date.valueOf("2022-10-10");

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("year", "2020", "amount", "100"),
                Map.of("year", "2021", "amount", "200")
        );

        // Setup - expectations
        given(expenseRepository.getYearlySumOfExpensesOfUserBeforeGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getYearlySumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(0)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }

    @Test
    public void testGettingYearlySumOfExpenses_WithAllParameters_ReturnsSumOfAllCategoryExpensesWithinThoseRangesAndCategories() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of("cat1", "cat2");
        Date fromParameter = Date.valueOf("2020-10-10");
        Date untilParameter = Date.valueOf("2022-10-10");

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("year", "2021", "amount", "200")
        );

        // Setup - expectations
        given(expenseRepository.getYearlySumOfExpensesOfUserByCategoryAndDateInterval(any(ExtraUser.class), any(), any(Date.class), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getYearlySumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(0)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }

    @Test
    public void testGettingYearlySumOfExpenses_WithNoUntilDateButWithFromDateAndWithCategoryParameters_ReturnsSumOfAllCategoryExpensesFromGivenDate() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of("cat1", "cat2");
        Date fromParameter = Date.valueOf("2020-10-10");
        Date untilParameter = null;

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("year", "2021", "amount", "200")
        );

        // Setup - expectations
        given(expenseRepository.getYearlySumOfExpensesOfUserAfterGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getYearlySumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, never()).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }

    @Test
    public void testGettingYearlySumOfExpenses_WithNoFromDateNoUntilDateButWithCategoryParameters_ReturnsSumOfAllCategoryExpenses() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of("cat1", "cat2");
        Date fromParameter = null;
        Date untilParameter = null;

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("year", "2020", "amount", "100"),
                Map.of("year", "2021", "amount", "200")
        );

        // Setup - expectations
        given(expenseRepository.getYearlySumOfExpensesByCategories(any(ExtraUser.class), any()))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getYearlySumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, never()).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }

    @Test
    public void testGettingYealySumOfExpenses_WithNoFromDateNoCategoryParametersButWithUntilDate_ReturnsSumOfAllExpensesUntilGivenDate() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of();
        Date fromParameter = null;
        Date untilParameter = Date.valueOf("2022-10-10");

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("year", "2020", "amount", "100"),
                Map.of("year", "2021", "amount", "200")
        );

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getYearlySumOfExpensesOfUserBeforeGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getYearlySumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(1)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }

    @Test
    public void testGettingYearlySumOfExpenses_WithDateRangesButNoCategories_ReturnsSumOfAllExpensesWithinThoseRanges() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of();
        Date fromParameter = Date.valueOf("2020-10-10");
        Date untilParameter = Date.valueOf("2022-10-10");

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("year", "2021", "amount", "200")
        );

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getYearlySumOfExpensesOfUserByCategoryAndDateInterval(any(ExtraUser.class), any(), any(Date.class), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getYearlySumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(1)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }

    @Test
    public void testGettingYearlySumOfExpenses_WithNoUntilDateNoCategoryParametersButWithFromDate_ReturnsSumOfAllExpensesFromGivenDate() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> categoriesParameter = List.of();
        Date fromParameter = Date.valueOf("2020-10-10");
        Date untilParameter = null;

        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("year", "2021", "amount", "200"),
                Map.of("year", "2022", "amount", "300")
        );

        // Setup - expectations
        given(expenseRepository.findAllDistinctCategoriesByUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getYearlySumOfExpensesOfUserAfterGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getYearlySumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(expenseRepository, times(1)).findAllDistinctCategoriesByUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }
}
