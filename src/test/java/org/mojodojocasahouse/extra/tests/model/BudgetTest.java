package org.mojodojocasahouse.extra.tests.model;

import java.math.BigDecimal;
import java.sql.Date;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.dto.requests.BudgetAddingRequest;
import org.mojodojocasahouse.extra.dto.model.BudgetDTO;
import org.mojodojocasahouse.extra.dto.requests.BudgetEditingRequest;
import org.mojodojocasahouse.extra.model.Budget;
import org.mojodojocasahouse.extra.model.Category;
import org.mojodojocasahouse.extra.model.ExtraUser;

public class BudgetTest {
    @Test
    void testGettingIdOfMadridTripBudgetReturnsNull() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Budget unmanagedBudget = new Budget(
            unmanagedUser,
            "Madrid trip",
            new BigDecimal(0),
            Date.valueOf("2018-12-9"),
            Date.valueOf("2023-12-9"),
            customCategory
        );

        Assertions.assertThat(unmanagedBudget.getId()).isNull();
    }
     
    @Test
    void testGettingUserOfMadridTripBudgetReturnsMichaelJordan() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Budget unmanagedBudget = new Budget(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(0),
                Date.valueOf("2018-12-9"),
                Date.valueOf("2023-12-9"),
                customCategory
        );

        Assertions.assertThat(unmanagedBudget.getUser()).isEqualTo(unmanagedUser);
    }
    
    @Test
    void testGettingConceptOfMadridTripBudgetReturnsMadridTrip() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Budget unmanagedBudget = new Budget(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(0),
                Date.valueOf("2018-12-9"),
                Date.valueOf("2023-12-9"),
                customCategory
        );
        Assertions.assertThat(unmanagedBudget.getName()).isEqualTo("Madrid trip");
    }

    @Test
    void testGettingCurrentAmountOfMadridTripReturnsZero() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Budget unmanagedBudget = new Budget(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(0),
                Date.valueOf("2018-12-9"),
                Date.valueOf("2023-12-9"),
                customCategory
        );

        Assertions.assertThat(unmanagedBudget.getCurrentAmount()).isEqualTo(new BigDecimal(0));
    }

    @Test
    void testGettingLimitAmountOfMadridTripReturnsOneHundred() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Budget unmanagedBudget = new Budget(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(0),
                Date.valueOf("2018-12-9"),
                Date.valueOf("2023-12-9"),
                customCategory
        );

        Assertions.assertThat(unmanagedBudget.getCurrentAmount()).isEqualTo(new BigDecimal(0));
    }

    @Test
    void testGettingStartingDateOfMadridTripExpenseReturnsNinthOfDecemberTwentyEighteen() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Budget unmanagedBudget = new Budget(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(0),
                Date.valueOf("2018-12-9"),
                Date.valueOf("2023-12-9"),
                customCategory
        );

        Assertions.assertThat(unmanagedBudget.getStartingDate()).isEqualTo(Date.valueOf("2018-12-9"));
    }

    @Test
    void testGettingLimitDateOfMadridTripExpenseReturnsNinthOfDecemberTwentyTwentyThree() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Budget unmanagedBudget = new Budget(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(0),
                Date.valueOf("2018-12-9"),
                Date.valueOf("2023-12-9"),
                customCategory
        );

        Assertions.assertThat(unmanagedBudget.getLimitDate()).isEqualTo(Date.valueOf("2023-12-9"));
    }

    @Test
    void testGettingCategoryOfMadridTripExpenseReturnsTravel() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Budget unmanagedBudget = new Budget(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(0),
                Date.valueOf("2018-12-9"),
                Date.valueOf("2023-12-9"),
                customCategory
        );

        Assertions.assertThat(unmanagedBudget.getCategory()).isEqualTo(customCategory);
    }

    @Test
    void testExtraExpenseCanBeCastedToDTO() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("Travel", (short) 2, unmanagedUser);
        BudgetAddingRequest unmanagedRequest = new BudgetAddingRequest(
                        "Paris Trip",
                        new BigDecimal(100),
                        Date.valueOf("2018-12-9"),
                        Date.valueOf("2023-12-9"),
                        customCategory.getName(),
                        customCategory.getIconId()
        );
        Budget unmanagedExpense = Budget.from(
                unmanagedRequest,
                customCategory,
                unmanagedUser
        );
        Assertions.assertThat(unmanagedExpense.asDto()).isInstanceOf(BudgetDTO.class);
    }
    


}
