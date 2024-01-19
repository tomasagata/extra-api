package org.mojodojocasahouse.extra.tests.model;

import java.math.BigDecimal;
import java.sql.Date;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.dto.requests.ExpenseAddingRequest;
import org.mojodojocasahouse.extra.dto.model.ExpenseDTO;
import org.mojodojocasahouse.extra.dto.requests.ExpenseEditingRequest;
import org.mojodojocasahouse.extra.model.Budget;
import org.mojodojocasahouse.extra.model.Category;
import org.mojodojocasahouse.extra.model.Expense;
import org.mojodojocasahouse.extra.model.ExtraUser;

public class ExpenseTest {
    @Test
    void testGettingIdOfMadridTripExpenseReturnsNull() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Expense unmanagedExpense = new Expense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                customCategory
        );

        Assertions.assertThat(unmanagedExpense.getId()).isNull();
    }
    
    @Test
    void testGettingUserOfMadridTripExpenseReturnsMichaelJordan() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Expense unmanagedExpense = new Expense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                customCategory
        );

        Assertions.assertThat(unmanagedExpense.getUser()).isEqualTo(unmanagedUser);
    }
    @Test
    void testGettingConceptOfMadridTripExpenseReturnsMadridTrip() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Expense unmanagedExpense = new Expense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                customCategory
        );

        Assertions.assertThat(unmanagedExpense.getConcept()).isEqualTo("Madrid trip");
    }
    @Test
    void testGettingAmountOfMadridTripReturnsOneHundred() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Expense unmanagedExpense = new Expense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                customCategory
        );

        Assertions.assertThat(unmanagedExpense.getAmount()).isEqualTo(new BigDecimal(100));
    }
    @Test
    void testGettingDateOfMadridTripExpenseReturnsNinthOfDecemberTwentyEighteen() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Expense unmanagedExpense = new Expense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                customCategory
        );

        Assertions.assertThat(unmanagedExpense.getDate()).isEqualTo(Date.valueOf("2018-12-9"));
    }

    @Test
    void testGettingIdOfMadridTripExpenseReturnsTravel() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Expense unmanagedExpense = new Expense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                customCategory
        );

        Assertions.assertThat(unmanagedExpense.getCategory()).isEqualTo(customCategory);
    }

    @Test
    void testUpdatingJustTheAmountModifiesOnlyTheAmount() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Expense unmanagedExpense = new Expense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                customCategory
        );

        unmanagedExpense.update(
                null,
                new BigDecimal(100),
                null,
                null
        );
        Assertions.assertThat(unmanagedExpense.getAmount()).isEqualTo((BigDecimal.valueOf(100)));
    }

    @Test
    void testUpdatingJustTheConceptModifiesOnlyTheConcept() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Expense unmanagedExpense = new Expense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                customCategory
        );

        unmanagedExpense.update(
                "Paris Trip",
                null,
                null,
                null
        );
        Assertions.assertThat(unmanagedExpense.getConcept()).isEqualTo(("Paris Trip"));
    }

    @Test
    void testUpdatingJustTheDateModifiesOnlyTheDate() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Expense unmanagedExpense = new Expense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                customCategory
        );

        unmanagedExpense.update(
                null,
                null,
                Date.valueOf("2023-12-09"),
                null
        );
        Assertions.assertThat(unmanagedExpense.getDate()).isEqualTo(Date.valueOf("2023-12-09"));
    }

    @Test
    void testUpdatingCategoryAndIconIDModifiesThoseFields() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Expense unmanagedExpense = new Expense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                customCategory
        );
        Category newCategory = new Category("Travel", (short) 2, unmanagedUser);

        unmanagedExpense.update(
                null,
                null,
                null,
                newCategory
        );
        Assertions.assertThat(unmanagedExpense.getCategory()).isEqualTo(newCategory);
    }
    

    @Test
    void testExtraExpenseCanBeCastedToDTO() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        Category customCategory = new Category("test1", (short) 1, unmanagedUser);
        Expense unmanagedExpense = new Expense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                customCategory
        );
        ExpenseDTO unmanagedExpenseDto = new ExpenseDTO(
                null,
                null,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                customCategory.asDto()
        );

        Assertions.assertThat(unmanagedExpense.asDto()).isEqualTo(unmanagedExpenseDto);
    }

}
