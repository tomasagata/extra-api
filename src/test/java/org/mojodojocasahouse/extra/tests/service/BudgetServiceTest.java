package org.mojodojocasahouse.extra.tests.service;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mojodojocasahouse.extra.dto.model.BudgetDTO;
import org.mojodojocasahouse.extra.dto.requests.BudgetAddingRequest;
import org.mojodojocasahouse.extra.dto.responses.ApiResponse;
import org.mojodojocasahouse.extra.exception.BudgetNotFoundException;
import org.mojodojocasahouse.extra.exception.ConflictingBudgetException;
import org.mojodojocasahouse.extra.model.*;
import org.mojodojocasahouse.extra.repository.BudgetRepository;
import org.mojodojocasahouse.extra.repository.ExpenseRepository;
import org.mojodojocasahouse.extra.repository.TransactionRepository;
import org.mojodojocasahouse.extra.service.BudgetService;
import org.mojodojocasahouse.extra.service.CategoryService;
import org.springframework.boot.test.json.JacksonTester;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceTest {


    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BudgetService budgetService;


    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }


    @Test
    public void testGettingAllBudgetByExistingUserIdReturnsList() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        Budget savedBudget1 = new Budget(user,"Name", new BigDecimal("10.11"), Date.valueOf("2023-09-11"), Date.valueOf("2023-09-11"), new Category("test",(short) 1, user));
        Budget savedBudget2 = new Budget(user,"Name", new BigDecimal("10.11"), Date.valueOf("2023-09-11"), Date.valueOf("2023-09-11"), new Category("test",(short) 1, user));

        List<Budget> expectedBudget = List.of(
                savedBudget1, savedBudget2
        );

        // Setup - expectations
        given(budgetRepository.findAllBudgetsByUser(any())).willReturn(expectedBudget);

        // exercise
        List<BudgetDTO> foundBudgetDtos = budgetService.getAllBudgetsByUserId(user);

        // verify
        Assertions
                .assertThat(foundBudgetDtos)
                .containsExactlyInAnyOrder(savedBudget1.asDto(), savedBudget2.asDto());
    }

    @Test
    public void testGettingAllBudgetByNonExistingUserIdReturnsEmptyList() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );

        // Setup - expectations
        given(budgetRepository.findAllBudgetsByUser(any())).willReturn(List.of());

        // exercise
        List<BudgetDTO> foundBudget = budgetService.getAllBudgetsByUserId(user);

        // verify
        Assertions.assertThat(foundBudget).isEqualTo(List.of());
    }

    @Test
    public void testAddingABudgetToExistingUserWithNoDateOverlappingExpensesReturnsSuccessfulResponse() {
        // Setup - data
        BudgetAddingRequest request = new BudgetAddingRequest(
                "Name",
                new BigDecimal("10.11"),
                Date.valueOf("2023-09-11"),
                Date.valueOf("2023-09-11"),
                "test",
                (short) 1
        );
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        Category existingOrNewCategory = new Category("test", (short) 1, user);
        List<Transaction> overlappingExpenses = new ArrayList<>();
        Budget savedBudget = new Budget(
                user,
                "test budget",
                BigDecimal.TEN,
                Date.valueOf("2023-09-11"),
                Date.valueOf("2023-09-11"),
                existingOrNewCategory
        );
        ApiResponse expectedResponse = new ApiResponse(
                "Budget added successfully!"
        );

        // Setup - expectations
        given(
                categoryService.fetchOrCreateCategoryFromUserAndNameAndIconId(any(), any(), any())
        ).willReturn(existingOrNewCategory);
        given(
                transactionRepository.getTransactionsByUserAndCategoryAndDateInterval(any(), any(), any(), any())
        ).willReturn(overlappingExpenses);
        given(
                budgetRepository.save(any())
        ).willReturn(savedBudget);

        // exercise
        ApiResponse actualResponse = budgetService.addBudget(user, request);

        // verify
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    public void testAddingABudgetToExistingUserWithOverlappingExpenses_LinksThemToTheNewBudget_ReturnsSuccessfulResponse() {
        // Setup - data
        BudgetAddingRequest request = new BudgetAddingRequest(
                "Name",
                new BigDecimal("10.11"),
                Date.valueOf("2023-09-11"),
                Date.valueOf("2023-09-11"),
                "test",
                (short) 1
        );
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        Category existingOrNewCategory = new Category("test", (short) 1, user);
        Expense existingExpense1 = new Expense(user, "test1", BigDecimal.TEN, Date.valueOf("2023-09-11"), existingOrNewCategory);
        Expense existingExpense2 = new Expense(user, "test2", BigDecimal.TWO, Date.valueOf("2023-09-11"), existingOrNewCategory);
        List<Transaction> overlappingExpenses = List.of(
                existingExpense1,
                existingExpense2
        );
        Budget savedBudget = new Budget(
                user,
                "test budget",
                BigDecimal.TEN,
                Date.valueOf("2023-09-11"),
                Date.valueOf("2023-09-11"),
                existingOrNewCategory
        );
        ApiResponse expectedResponse = new ApiResponse(
                "Budget added successfully!"
        );

        // Setup - expectations
        given(
                categoryService.fetchOrCreateCategoryFromUserAndNameAndIconId(any(), any(), any())
        ).willReturn(existingOrNewCategory);
        given(
                transactionRepository.getTransactionsByUserAndCategoryAndDateInterval(any(), any(), any(), any())
        ).willReturn(overlappingExpenses);
        given(
                budgetRepository.save(any())
        ).willReturn(savedBudget);

        // exercise
        ApiResponse actualResponse = budgetService.addBudget(user, request);

        // verify
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
        Assertions.assertThat(existingExpense1.getLinkedBudget()).isNotNull();
        Assertions.assertThat(existingExpense2.getLinkedBudget()).isNotNull();
    }

    @Test
    public void testAddingABudgetWhenAnotherIsActiveThrowsConflictingBudgetException() {
        // Setup - data
        BudgetAddingRequest request = new BudgetAddingRequest(
                "Name",
                new BigDecimal("10.11"),
                Date.valueOf("2023-09-11"),
                Date.valueOf("2023-09-11"),
                "test",
                (short) 1
        );
        Budget conflictingBudget = new Budget();
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );

        // Setup - expectations
        given(budgetRepository.findOverlappingBudgetsByUserAndCategory(any(), any(), any(), any()))
                .willReturn(List.of(conflictingBudget));

        // exercise & verify
        Assertions
                .assertThatThrownBy(() -> budgetService.addBudget(user, request))
                .isInstanceOf(ConflictingBudgetException.class);
    }

    @Test
    public void testGettingAllBudgetsByCategoryAndUserReturnsAListOfBudgets(){
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        Budget savedBudget1 = new Budget(
                user,
                "Name",
                new BigDecimal("10.11"),
                Date.valueOf("2023-09-11"),
                Date.valueOf("2023-09-11"),
                new Category("test1", (short) 1, user)
        );
        List<BudgetDTO> expectedDtos = List.of(savedBudget1.asDto());

        given(budgetRepository.findAllBudgetsByUser(any())).willReturn(List.of(savedBudget1));

        List<BudgetDTO> foundBudget = budgetService.getAllBudgetsByUserId(user);

        Assertions.assertThat(foundBudget).containsExactlyInAnyOrder(expectedDtos.toArray(BudgetDTO[]::new));
    }

    @Test
    public void testGettingExistingBudgetByIdReturnsItsDTO() {
        // Setup - data
        Long existingBudgetId = 1L;
        ExtraUser linkedUser = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        Budget existingBudget = new Budget(
                linkedUser,
                "Name",
                new BigDecimal("10.11"),
                Date.valueOf("2023-09-11"),
                Date.valueOf("2023-09-11"),
                new Category("test1", (short) 1, linkedUser)
        );

        // Setup - expectations
        given(budgetRepository.findById(any())).willReturn(Optional.of(existingBudget));

        // exercise & verify
        BudgetDTO result = budgetService.getBudgetById(existingBudgetId);
        Assertions.assertThat(result).isEqualTo(existingBudget.asDto());
    }

    @Test
    public void testGettingNonExistingBudgetByIdReturnsItsDTO() {
        // Setup - data
        Long nonExistingBudgetId = 1L;

        // Setup - expectations
        given(budgetRepository.findById(any()))
                .willReturn(Optional.empty());

        // exercise & verify
        Assertions
                .assertThatThrownBy(() -> budgetService.getBudgetById(nonExistingBudgetId))
                .isInstanceOf(BudgetNotFoundException.class);
    }

    @Test
    public void gettingExistingActiveBudgetReturnsItsDTO() {
        // Setup - data
        ExtraUser linkedUser = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        Category customCategory = new Category("test1", (short) 1, linkedUser);
        Budget existingBudget = new Budget(
                linkedUser,
                "Name",
                new BigDecimal("0"),
                Date.valueOf("2023-09-30"),
                Date.valueOf("2023-09-10"),
                customCategory
        );

        // Setup - expectations
        given(budgetRepository.findActiveBudgetByUserAndCategoryAndDate(any(), any(), any()))
                .willReturn(List.of(existingBudget));

        // exercise & verify
        BudgetDTO result = budgetService.getActiveBudgetByCategoryAndDate(linkedUser, customCategory, Date.valueOf("2023-09-15"));
        Assertions.assertThat(result).isEqualTo(existingBudget.asDto());
    }

    @Test
    public void gettingNonExistingActiveBudgetReturnsNull() {
        // Setup - data
        ExtraUser linkedUser = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        Category customCategory = new Category("test1", (short) 1, linkedUser);

        // Setup - expectations
        given(budgetRepository.findActiveBudgetByUserAndCategoryAndDate(any(), any(), any()))
                .willReturn(List.of());

        // exercise & verify
        BudgetDTO result = budgetService.getActiveBudgetByCategoryAndDate(linkedUser, customCategory, Date.valueOf("2023-09-15"));
        Assertions.assertThat(result).isNull();
    }

}
