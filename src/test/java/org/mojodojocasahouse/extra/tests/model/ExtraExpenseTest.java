package org.mojodojocasahouse.extra.tests.model;

import java.math.BigDecimal;
import java.sql.Date;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.dto.ExpenseAddingRequest;
import org.mojodojocasahouse.extra.dto.ExpenseDTO;
import org.mojodojocasahouse.extra.dto.ExpenseEditingRequest;
import org.mojodojocasahouse.extra.model.ExtraExpense;
import org.mojodojocasahouse.extra.model.ExtraUser;

public class ExtraExpenseTest {
    @Test
    void testGettingIdOfMadridTripExpenseReturnsNull() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        ExtraExpense unmanagedExpense = new ExtraExpense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                "test",
                (short) 1
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
        ExtraExpense unmanagedExpense = new ExtraExpense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                "test",
                (short) 1
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
        ExtraExpense unmanagedExpense = new ExtraExpense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                "test",
                (short) 1
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
        ExtraExpense unmanagedExpense = new ExtraExpense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                "test",
                (short) 1
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
        ExtraExpense unmanagedExpense = new ExtraExpense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                "test",
                (short) 1
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
        ExtraExpense unmanagedExpense = new ExtraExpense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                "travel",
                (short) 1
        );

        Assertions.assertThat(unmanagedExpense.getCategory()).isEqualTo("travel");
    }

    @Test
    void testGettingIconofMadridTripExpenseReturnsShortOne() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        ExtraExpense unmanagedExpense = new ExtraExpense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(100),
                Date.valueOf("2018-12-9"),
                "test",
                (short) 1
        );

        Assertions.assertThat(unmanagedExpense.getIconId()).isEqualTo((short) 1);
    }

    @Test
    void testUpdatingJustTheAmountModifiesOnlyTheAmount() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        ExtraExpense unmanagedExpense = new ExtraExpense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(99),
                Date.valueOf("2018-12-9"),
                "test",
                (short) 1
        );
       unmanagedExpense.updateFrom(
                new ExpenseEditingRequest(
                        null,
                        new BigDecimal(100),
                        null,
                        null,
                        null
                ),
                unmanagedUser
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
        ExtraExpense unmanagedExpense = new ExtraExpense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(99),
                Date.valueOf("2018-12-9"),
                "test",
                (short) 1
        );
       unmanagedExpense.updateFrom(
                new ExpenseEditingRequest(
                        "Paris Trip",
                        null,
                        null,
                        null,
                        null
                ),
                unmanagedUser
        );
        Assertions.assertThat(unmanagedExpense.getConcept()).isEqualTo(("Paris Trip"));
    }

    @Test
    void testUpdatingCategoryAndIconIDModifiesThoseFields() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        ExtraExpense unmanagedExpense = new ExtraExpense(
                unmanagedUser,
                "Madrid trip",
                new BigDecimal(99),
                Date.valueOf("2018-12-9"),
                "test",
                (short) 1
        );
       unmanagedExpense.updateFrom(
                new ExpenseEditingRequest(
                        null,
                        null,
                        null,
                        "Travel",
                        (short) 2
                ),
                unmanagedUser
        );
        Assertions.assertThat(unmanagedExpense.getCategory()).isEqualTo("Travel");
        Assertions.assertThat(unmanagedExpense.getIconId()).isEqualTo((short) 2);
    }
    

    @Test
    void testExtraExpenseCanBeCreatedFromDTO() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
       ExtraExpense unmanagedExpense = ExtraExpense.from(
                new ExpenseAddingRequest(
                        "Paris Trip",
                        new BigDecimal(100),
                        Date.valueOf("2018-12-9"),
                        "Travel",
                        (short) 2
                ),
                unmanagedUser
        );
        Assertions.assertThat(unmanagedExpense.getAmount()).isEqualTo((BigDecimal.valueOf(100)));
    }
    

    @Test
    void testExtraExpenseCanBeCastedToDTO() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        ExpenseAddingRequest unamanagedRequest = new ExpenseAddingRequest(
                        "Paris Trip",
                        new BigDecimal(100),
                        Date.valueOf("2018-12-9"),
                        "Travel",
                        (short) 2
        );
       ExtraExpense unmanagedExpense = ExtraExpense.from(
                unamanagedRequest,
                unmanagedUser
        );
        Assertions.assertThat(unmanagedExpense.asDto()).isInstanceOf(ExpenseDTO.class);
    }

}
