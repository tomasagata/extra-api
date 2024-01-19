package org.mojodojocasahouse.extra.tests.service;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mojodojocasahouse.extra.dto.model.CategoryWithIconDTO;
import org.mojodojocasahouse.extra.dto.model.ExpenseDTO;
import org.mojodojocasahouse.extra.dto.requests.ExpenseAddingRequest;
import org.mojodojocasahouse.extra.dto.requests.ExpenseEditingRequest;
import org.mojodojocasahouse.extra.dto.responses.ApiResponse;
import org.mojodojocasahouse.extra.model.Category;
import org.mojodojocasahouse.extra.model.Expense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.BudgetRepository;
import org.mojodojocasahouse.extra.repository.ExpenseRepository;
import org.mojodojocasahouse.extra.service.BudgetService;
import org.mojodojocasahouse.extra.service.CategoryService;
import org.mojodojocasahouse.extra.service.ExpenseService;
import org.springframework.boot.test.json.JacksonTester;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpensesServiceTest {


    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BudgetService budgetService;

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
        Category customCategory = new Category("test", (short) 1, user);
        Expense savedExpense1 = new Expense(user, "Another Concept", new BigDecimal("10.11"), Date.valueOf("2023-09-11"), customCategory);
        Expense savedExpense2 = new Expense(user, "Another Concept", new BigDecimal("10.12"), Date.valueOf("2023-09-12"), customCategory);

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
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        Category customCategory = new Category("test", (short) 1, user);
        ExpenseAddingRequest request = new ExpenseAddingRequest(
                "A Concept",
                new BigDecimal("10.12"),
                Date.valueOf("2023-09-19"),
                customCategory.getName(),
                customCategory.getIconId()
        );
        ApiResponse expectedResponse = new ApiResponse(
                "Expense added successfully!"
        );
        Expense savedExpense = new Expense(
                user,
                "A concept",
                new BigDecimal("10.12"),
                Date.valueOf("2023-09-19"),
                customCategory
        );

        // Setup - expectations
        given(expenseRepository.save(any())).willReturn(savedExpense);

        // exercise
        ApiResponse actualResponse = expenseService.addExpense(user, request);

        // verify
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    public void testExpenseCanBeEdited(){
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        Category customCategory = new Category("test", (short) 1, user);
        Expense savedExpense1 = new Expense(user,"Another Concept", new BigDecimal("10.11"), Date.valueOf("2023-09-11"), customCategory);
        given(expenseRepository.findById(any())).willReturn(java.util.Optional.of(savedExpense1));
        Long id = (long) 0;
        ExpenseEditingRequest request = new ExpenseEditingRequest(
                "Another concept",
                null,
                null,
                null,
                null
        );
        ApiResponse expectedResponse = new ApiResponse(
                "Expense edited successfully!"
        );
        ApiResponse actualResponse = expenseService.editExpense(id, request);
        
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
        Category customCategory = new Category("test", (short) 1, user);
        Expense savedExpense1 = new Expense(user,"Another Concept", new BigDecimal("10.11"), Date.valueOf("2023-09-11"), customCategory);
        Long expense_id = savedExpense1.getId();

        given(expenseRepository.findById(any())).willReturn(Optional.of(savedExpense1));

        Assertions.assertThatNoException().isThrownBy(() -> expenseService.deleteById(expense_id));
    }
    @Test
    public void testExpenseHaveOwner(){
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        Category customCategory = new Category("test", (short) 1, user);
        Expense savedExpense1 = new Expense(user,"Another Concept", new BigDecimal("10.11"), Date.valueOf("2023-09-11"), customCategory);
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

        List<String> existingCategoryNames = List.of("cat1", "cat2", "cat3");
        List<Map<String, String>> expectedResults = List.of(
                Map.of("category", "cat1", "amount", "100"),
                Map.of("category", "cat2", "amount", "200"),
                Map.of("category", "cat3", "amount", "300")
        );

        // Setup - expectations
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
                .willReturn(existingCategoryNames);
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

        // List<String> existingCategories = List.of("cat1", "cat2", "cat3");
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
        verify(categoryService, times(0)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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

        // List<String> existingCategories = List.of("cat1", "cat2", "cat3");
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
        verify(categoryService, times(0)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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

        // List<String> existingCategories = List.of("cat1", "cat2", "cat3");
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
        verify(categoryService, never()).getAllCategoryNamesOfUser(any(ExtraUser.class));
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

        // List<String> existingCategories = List.of("cat1", "cat2", "cat3");
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
        verify(categoryService, never()).getAllCategoryNamesOfUser(any(ExtraUser.class));
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
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getSumOfExpensesOfUserBeforeGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getSumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(categoryService, times(1)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getSumOfExpensesOfUserByCategoryAndDateInterval(any(ExtraUser.class), any(), any(Date.class), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getSumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(categoryService, times(1)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getSumOfExpensesOfUserAfterGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getSumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(categoryService, times(1)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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

        Category cat1 = new Category("cat1", (short) 1, user);
        Category cat2 = new Category("cat2", (short) 2, user);
        Category cat3 = new Category("cat3", (short) 3, user);
        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 1", new BigDecimal("100.0"), Date.valueOf("2020-09-12"), cat1),
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2),
                new Expense(user, "Expense 3", new BigDecimal("300.0"), Date.valueOf("2023-09-12"), cat3)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getExpensesOfUserByCategory(any(ExtraUser.class), any()))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(categoryService, times(1)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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

        Category cat1 = new Category("cat1", (short) 1, user);
        Category cat2 = new Category("cat2", (short) 2, user);
        Category cat3 = new Category("cat3", (short) 3, user);
        // List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 1", new BigDecimal("100.0"), Date.valueOf("2020-09-12"), cat1),
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(expenseRepository.getExpensesOfUserBeforeGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(categoryService, times(0)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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

        Category cat1 = new Category("cat1", (short) 1, user);
        Category cat2 = new Category("cat2", (short) 2, user);
        Category cat3 = new Category("cat3", (short) 3, user);
        // List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(expenseRepository.getExpensesOfUserByCategoriesAndDateInterval(any(ExtraUser.class), any(), any(Date.class), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(categoryService, times(0)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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

        Category cat1 = new Category("cat1", (short) 1, user);
        Category cat2 = new Category("cat2", (short) 2, user);
        Category cat3 = new Category("cat3", (short) 3, user);
        // List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(expenseRepository.getExpensesOfUserAfterGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(categoryService, times(0)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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

        Category cat1 = new Category("cat1", (short) 1, user);
        Category cat2 = new Category("cat2", (short) 2, user);
        Category cat3 = new Category("cat3", (short) 3, user);
        // List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 1", new BigDecimal("100.0"), Date.valueOf("2020-09-12"), cat1),
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(expenseRepository.getExpensesOfUserByCategory(any(ExtraUser.class), any()))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(categoryService, times(0)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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

        Category cat1 = new Category("cat1", (short) 1, user);
        Category cat2 = new Category("cat2", (short) 2, user);
        Category cat3 = new Category("cat3", (short) 3, user);
        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 1", new BigDecimal("100.0"), Date.valueOf("2020-09-12"), cat1),
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getExpensesOfUserBeforeGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(categoryService, times(1)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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

        Category cat1 = new Category("cat1", (short) 1, user);
        Category cat2 = new Category("cat2", (short) 2, user);
        Category cat3 = new Category("cat3", (short) 3, user);
        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getExpensesOfUserByCategoriesAndDateInterval(any(ExtraUser.class), any(), any(Date.class), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(categoryService, times(1)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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

        Category cat1 = new Category("cat1", (short) 1, user);
        Category cat2 = new Category("cat2", (short) 2, user);
        Category cat3 = new Category("cat3", (short) 3, user);
        List<String> existingCategories = List.of("cat1", "cat2", "cat3");
        List<Expense> expectedExpenseResults = List.of(
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2),
                new Expense(user, "Expense 3", new BigDecimal("300.0"), Date.valueOf("2023-09-12"), cat2)
        );
        List<ExpenseDTO> expectedResults = expectedExpenseResults.stream().map(Expense::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getExpensesOfUserAfterGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<ExpenseDTO> results = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(categoryService, times(1)).getAllCategoryNamesOfUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);
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
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
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

        // List<String> existingCategories = List.of("cat1", "cat2", "cat3");
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
        verify(categoryService, times(0)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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

        // List<String> existingCategories = List.of("cat1", "cat2", "cat3");
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
        verify(categoryService, times(0)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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

        // List<String> existingCategories = List.of("cat1", "cat2", "cat3");
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
        verify(categoryService, never()).getAllCategoryNamesOfUser(any(ExtraUser.class));
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

        // List<String> existingCategories = List.of("cat1", "cat2", "cat3");
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
        verify(categoryService, never()).getAllCategoryNamesOfUser(any(ExtraUser.class));
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
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getYearlySumOfExpensesOfUserBeforeGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getYearlySumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(categoryService, times(1)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getYearlySumOfExpensesOfUserByCategoryAndDateInterval(any(ExtraUser.class), any(), any(Date.class), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getYearlySumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(categoryService, times(1)).getAllCategoryNamesOfUser(any(ExtraUser.class));
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
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(expenseRepository.getYearlySumOfExpensesOfUserAfterGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = expenseService
                .getYearlySumOfExpensesOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(categoryService, times(1)).getAllCategoryNamesOfUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }
}
