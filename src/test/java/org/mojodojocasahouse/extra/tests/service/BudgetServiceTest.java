package org.mojodojocasahouse.extra.tests.service;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mojodojocasahouse.extra.dto.*;
import org.mojodojocasahouse.extra.exception.BudgetNotFoundException;
import org.mojodojocasahouse.extra.exception.ConflictingBudgetException;
import org.mojodojocasahouse.extra.exception.EmailException;
import org.mojodojocasahouse.extra.model.Budget;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.BudgetRepository;
import org.mojodojocasahouse.extra.service.BudgetService;
import org.springframework.boot.test.json.JacksonTester;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceTest {


    @Mock
    private BudgetRepository budgetRepository;

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
        Budget savedBudget1 = new Budget(user,"Name", new BigDecimal("10.11"), new BigDecimal("10.11"), Date.valueOf("2023-09-11"), Date.valueOf("2023-09-11"), "test",(short) 1);
        Budget savedBudget2 = new Budget(user,"Name", new BigDecimal("10.11"), new BigDecimal("10.11"), Date.valueOf("2023-09-11"), Date.valueOf("2023-09-11"), "test",(short) 1);

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
    public void testAddingABudgetToExistingUserReturnsSuccessfulResponse() {
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
        ApiResponse expectedResponse = new ApiResponse(
                "Budget added successfully!"
        );

        // exercise
        ApiResponse actualResponse = budgetService.addBudget(user, request);

        // verify
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
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
        given(budgetRepository.findBudgetByUserAndCategoryAndStartDateAndEndDate(any(), any(), any(), any()))
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
                new BigDecimal("10.11"),
                Date.valueOf("2023-09-11"),
                Date.valueOf("2023-09-11"),
                "test1",
                (short) 1
        );
        List<BudgetDTO> expectedDtos = List.of(savedBudget1.asDto());

        given(budgetRepository.findAllBudgetsByUser(any())).willReturn(List.of(savedBudget1));

        List<BudgetDTO> foundBudget = budgetService.getAllBudgetsByUserId(user);

        Assertions.assertThat(foundBudget).containsExactlyInAnyOrder(expectedDtos.toArray(BudgetDTO[]::new));
    }

    @Test
    public void testBudgetCanBeEdited(){
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
                new BigDecimal("10.11"),
                Date.valueOf("2023-09-11"),
                Date.valueOf("2023-09-11"),
                "test1",
                (short) 1
        );
        given(budgetRepository.findById(any())).willReturn(java.util.Optional.of(savedBudget1));
        Long id = (long) 0;
        BudgetEditingRequest request = new BudgetEditingRequest(
                "Nameson",
                null,
                null, 
                null,
                null,
                null, 
                null
        );
        ApiResponse expectedResponse = new ApiResponse(
                "Budget edited successfully!"
        );
        ApiResponse actualResponse = budgetService.editBudget(user,id, request);
        
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }  

    @Test
    public void testBudgetCanBeDeleted(){
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
                new BigDecimal("10.11"),
                Date.valueOf("2023-09-11"),
                Date.valueOf("2023-09-11"),
                "test1",
                (short) 1
        );
        budgetRepository.save(savedBudget1);
        Long id = (long) 0;
        budgetService.deleteById(id);
        Assertions.assertThat(budgetService.existsById(id)).isEqualTo(false);
    }
    @Test
    public void testBudgetHaveOwner(){
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
                new BigDecimal("10.11"),
                Date.valueOf("2023-09-11"),
                Date.valueOf("2023-09-11"),
                "test1",
                (short) 1
        );
        budgetRepository.save(savedBudget1);
        Long id = (long) 0;
        Assertions.assertThat(budgetService.isOwner(user, id)).isEqualTo(false);
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
                new BigDecimal("10.11"),
                Date.valueOf("2023-09-11"),
                Date.valueOf("2023-09-11"),
                "test1",
                (short) 1
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
    public void addingAmountToActiveBudgetUpdatesOldBudget() {
        // Setup - data
        ExtraUser linkedUser = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        Budget existingBudget = new Budget(
                linkedUser,
                "Name",
                new BigDecimal("0"),
                new BigDecimal("100"),
                Date.valueOf("2023-09-30"),
                Date.valueOf("2023-09-10"),
                "test1",
                (short) 1
        );

        // Setup - expectations
        given(budgetRepository.findActiveBudgetByUserAndCategoryAndDate(any(), any(), any()))
                .willReturn(List.of(existingBudget));

        // exercise & verify
        Assertions.assertThat(existingBudget.getCurrentAmount()).isEqualTo(new BigDecimal(0));
        budgetService.addToActiveBudget(linkedUser, new BigDecimal(10), "test1", Date.valueOf("2023-09-15"));
        Assertions.assertThat(existingBudget.getCurrentAmount()).isEqualTo(new BigDecimal(10));
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
        Budget existingBudget = new Budget(
                linkedUser,
                "Name",
                new BigDecimal("0"),
                new BigDecimal("100"),
                Date.valueOf("2023-09-30"),
                Date.valueOf("2023-09-10"),
                "test1",
                (short) 1
        );

        // Setup - expectations
        given(budgetRepository.findActiveBudgetByUserAndCategoryAndDate(any(), any(), any()))
                .willReturn(List.of(existingBudget));

        // exercise & verify
        BudgetDTO result = budgetService.getActiveBudgetByCategoryAndDate(linkedUser, "test1", Date.valueOf("2023-09-15"));
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

        // Setup - expectations
        given(budgetRepository.findActiveBudgetByUserAndCategoryAndDate(any(), any(), any()))
                .willReturn(List.of());

        // exercise & verify
        BudgetDTO result = budgetService.getActiveBudgetByCategoryAndDate(linkedUser, "test1", Date.valueOf("2023-09-15"));
        Assertions.assertThat(result).isNull();
    }

    @Test
    public void gettingAllCategoriesOfUserReturnsListOfStrings() {
        // Setup - data
        ExtraUser linkedUser = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> existingCategories = List.of("sample1", "sample2", "sample3");

        // Setup - expectations
        given(budgetRepository.findAllDistinctCategoriesByUser(any()))
                .willReturn(existingCategories);

        // exercise & verify
        List<String> results = budgetService.getAllCategories(linkedUser);
        Assertions.assertThat(results).isEqualTo(existingCategories);
    }

    @Test
    public void gettingAllCategoriesWithIconsOfUserReturnsListOfJsonObjects() {
        // Setup - data
        ExtraUser linkedUser = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<CategoryWithIconDTO> expectedResponse = List.of(
                new CategoryWithIconDTO("some category 1", (short)1),
                new CategoryWithIconDTO("some category 1", (short)2),
                new CategoryWithIconDTO("some category 1", (short)3)
        );

        // Setup - expectations
        given(budgetRepository.findAllDistinctCategoriesByUserWithIcons(any()))
                .willReturn(expectedResponse);

        // exercise & verify
        List<CategoryWithIconDTO> results = budgetService.getAllCategoriesWithIcons(linkedUser);
        Assertions.assertThat(results).isEqualTo(expectedResponse);
    }

}
