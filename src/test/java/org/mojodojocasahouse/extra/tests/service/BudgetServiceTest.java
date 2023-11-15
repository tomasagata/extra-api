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
import org.mojodojocasahouse.extra.dto.BudgetAddingRequest;
import org.mojodojocasahouse.extra.dto.BudgetDTO;
import org.mojodojocasahouse.extra.dto.BudgetEditingRequest;
import org.mojodojocasahouse.extra.model.ExtraBudget;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.BudgetRepository;
import org.mojodojocasahouse.extra.service.BudgetService;
import org.springframework.boot.test.json.JacksonTester;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
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
        ExtraBudget savedBudget1 = new ExtraBudget(user,"Name", new BigDecimal("10.11"), new BigDecimal("10.11"), Date.valueOf("2023-09-11"), Date.valueOf("2023-09-11"), "test",(short) 1);
        ExtraBudget savedBudget2 = new ExtraBudget(user,"Name", new BigDecimal("10.11"), new BigDecimal("10.11"), Date.valueOf("2023-09-11"), Date.valueOf("2023-09-11"), "test",(short) 1);

        List<ExtraBudget> expectedBudget = List.of(
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
                "Budget added succesfully!"
        );

        // exercise
        ApiResponse actualResponse = budgetService.addBudget(user, request);

        // verify
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    public void testGettingAllBudgetsByCategoryAndUserReturnsAListOfBudgets(){
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        ExtraBudget savedBudget1 = new ExtraBudget(
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
        ExtraBudget savedBudget1 = new ExtraBudget(
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
        ExtraBudget savedBudget1 = new ExtraBudget(
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
        ExtraBudget savedBudget1 = new ExtraBudget(
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
    

}
