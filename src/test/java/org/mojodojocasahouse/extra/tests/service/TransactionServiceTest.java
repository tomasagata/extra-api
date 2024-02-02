package org.mojodojocasahouse.extra.tests.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mojodojocasahouse.extra.dto.model.ExpenseDTO;
import org.mojodojocasahouse.extra.dto.model.TransactionDTO;
import org.mojodojocasahouse.extra.model.Category;
import org.mojodojocasahouse.extra.model.Expense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.model.Transaction;
import org.mojodojocasahouse.extra.repository.BudgetRepository;
import org.mojodojocasahouse.extra.repository.ExpenseRepository;
import org.mojodojocasahouse.extra.repository.TransactionRepository;
import org.mojodojocasahouse.extra.service.BudgetService;
import org.mojodojocasahouse.extra.service.CategoryService;
import org.mojodojocasahouse.extra.service.ExpenseService;
import org.mojodojocasahouse.extra.service.TransactionService;
import org.springframework.boot.test.json.JacksonTester;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
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
        List<Transaction> expectedExpenseResults = List.of(
                new Expense(user, "Expense 1", new BigDecimal("100.0"), Date.valueOf("2020-09-12"), cat1),
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2),
                new Expense(user, "Expense 3", new BigDecimal("300.0"), Date.valueOf("2023-09-12"), cat3)
        );
        List<TransactionDTO> expectedResults = expectedExpenseResults
                .stream()
                .map(Transaction::asDto)
                .collect(Collectors.toList());

        // Setup - expectations
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(transactionRepository.getTransactionsOfUserByCategory(any(ExtraUser.class), any()))
                .willReturn(expectedExpenseResults);

        // Execute
        List<TransactionDTO> results = transactionService
                .getTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

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
        List<Transaction> expectedExpenseResults = List.of(
                new Expense(user, "Expense 1", new BigDecimal("100.0"), Date.valueOf("2020-09-12"), cat1),
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2)
        );
        List<TransactionDTO> expectedResults = expectedExpenseResults
                .stream()
                .map(Transaction::asDto)
                .collect(Collectors.toList());

        // Setup - expectations
        given(transactionRepository.getTransactionsOfUserBeforeGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<TransactionDTO> results = transactionService
                .getTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

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
        List<Transaction> expectedExpenseResults = List.of(
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2)
        );
        List<TransactionDTO> expectedResults = expectedExpenseResults.stream().map(Transaction::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(transactionRepository.getTransactionsOfUserByCategoriesAndDateInterval(any(ExtraUser.class), any(), any(Date.class), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<TransactionDTO> results = transactionService
                .getTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

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
        List<Transaction> expectedExpenseResults = List.of(
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2)
        );
        List<TransactionDTO> expectedResults = expectedExpenseResults.stream().map(Transaction::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(transactionRepository.getTransactionsOfUserAfterGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<TransactionDTO> results = transactionService
                .getTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

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
        List<Transaction> expectedExpenseResults = List.of(
                new Expense(user, "Expense 1", new BigDecimal("100.0"), Date.valueOf("2020-09-12"), cat1),
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2)
        );
        List<TransactionDTO> expectedResults = expectedExpenseResults.stream().map(Transaction::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(transactionRepository.getTransactionsOfUserByCategory(any(ExtraUser.class), any()))
                .willReturn(expectedExpenseResults);

        // Execute
        List<TransactionDTO> results = transactionService
                .getTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

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
        List<Transaction> expectedExpenseResults = List.of(
                new Expense(user, "Expense 1", new BigDecimal("100.0"), Date.valueOf("2020-09-12"), cat1),
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2)
        );
        List<TransactionDTO> expectedResults = expectedExpenseResults.stream().map(Transaction::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(transactionRepository.getTransactionsOfUserBeforeGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<TransactionDTO> results = transactionService
                .getTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

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
        List<Transaction> expectedExpenseResults = List.of(
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2)
        );
        List<TransactionDTO> expectedResults = expectedExpenseResults.stream().map(Transaction::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(transactionRepository.getTransactionsOfUserByCategoriesAndDateInterval(any(ExtraUser.class), any(), any(Date.class), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<TransactionDTO> results = transactionService
                .getTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

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
        List<Transaction> expectedExpenseResults = List.of(
                new Expense(user, "Expense 2", new BigDecimal("200.0"), Date.valueOf("2022-09-12"), cat2),
                new Expense(user, "Expense 3", new BigDecimal("300.0"), Date.valueOf("2023-09-12"), cat2)
        );
        List<TransactionDTO> expectedResults = expectedExpenseResults.stream().map(Transaction::asDto).collect(Collectors.toList());

        // Setup - expectations
        given(categoryService.getAllCategoryNamesOfUser(any(ExtraUser.class)))
                .willReturn(existingCategories);
        given(transactionRepository.getTransactionsOfUserAfterGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedExpenseResults);

        // Execute
        List<TransactionDTO> results = transactionService
                .getTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

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
        given(transactionRepository.getYearlySumOfTransactionsByCategories(any(ExtraUser.class), any()))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = transactionService
                .getYearlySumOfTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

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
        given(transactionRepository.getYearlySumOfTransactionsOfUserBeforeGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = transactionService
                .getYearlySumOfTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

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
        given(transactionRepository.getYearlySumOfTransactionsOfUserByCategoryAndDateInterval(any(ExtraUser.class), any(), any(Date.class), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = transactionService
                .getYearlySumOfTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

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
        given(transactionRepository.getYearlySumOfTransactionsOfUserAfterGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = transactionService
                .getYearlySumOfTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

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
        given(transactionRepository.getYearlySumOfTransactionsByCategories(any(ExtraUser.class), any()))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = transactionService
                .getYearlySumOfTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

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
        given(transactionRepository.getYearlySumOfTransactionsOfUserBeforeGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = transactionService
                .getYearlySumOfTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

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
        given(transactionRepository.getYearlySumOfTransactionsOfUserByCategoryAndDateInterval(any(ExtraUser.class), any(), any(Date.class), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = transactionService
                .getYearlySumOfTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

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
        given(transactionRepository.getYearlySumOfTransactionsOfUserAfterGivenDate(any(ExtraUser.class), any(), any(Date.class)))
                .willReturn(expectedResults);

        // Execute
        List<Map<String, String>> results = transactionService
                .getYearlySumOfTransactionsOfUserByCategoriesAndDateRanges(user, categoriesParameter, fromParameter, untilParameter);

        // Verify
        verify(categoryService, times(1)).getAllCategoryNamesOfUser(any(ExtraUser.class));
        Assertions.assertThat(results).isEqualTo(expectedResults);

    }


}
