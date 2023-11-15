package org.mojodojocasahouse.extra.tests.model;

import java.math.BigDecimal;
import java.sql.Date;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.dto.BudgetAddingRequest;
import org.mojodojocasahouse.extra.dto.BudgetDTO;
import org.mojodojocasahouse.extra.dto.BudgetEditingRequest;
import org.mojodojocasahouse.extra.model.ExtraBudget;
import org.mojodojocasahouse.extra.model.ExtraUser;

public class ExtraBudgetTest {
    @Test
    void testGettingIdOfMadridTripBudgetReturnsNull() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        ExtraBudget unmanagedBudget = new ExtraBudget(
            unmanagedUser,
            "Madrid trip",
            new BigDecimal(0),
            new BigDecimal(100),
            Date.valueOf("2018-12-9"),
            Date.valueOf("2023-12-9"),
            "test",
            (short) 1
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
        ExtraBudget unmanagedBudget = new ExtraBudget(
            unmanagedUser,
            "Madrid trip",
            new BigDecimal(0),
            new BigDecimal(100),
            Date.valueOf("2018-12-9"),
            Date.valueOf("2023-12-9"),
            "test",
            (short) 1
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
        ExtraBudget unmanagedBudget = new ExtraBudget(
            unmanagedUser,
            "Madrid trip",
            new BigDecimal(0),
            new BigDecimal(100),
            Date.valueOf("2018-12-9"),
            Date.valueOf("2023-12-9"),
            "test",
            (short) 1
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
        ExtraBudget unmanagedBudget = new ExtraBudget(
            unmanagedUser,
            "Madrid trip",
            new BigDecimal(0),
            new BigDecimal(100),
            Date.valueOf("2018-12-9"),
            Date.valueOf("2023-12-9"),
            "test",
            (short) 1
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
        ExtraBudget unmanagedBudget = new ExtraBudget(
            unmanagedUser,
            "Madrid trip",
            new BigDecimal(0),
            new BigDecimal(100),
            Date.valueOf("2018-12-9"),
            Date.valueOf("2023-12-9"),
            "test",
            (short) 1
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
        ExtraBudget unmanagedBudget = new ExtraBudget(
            unmanagedUser,
            "Madrid trip",
            new BigDecimal(0),
            new BigDecimal(100),
            Date.valueOf("2023-12-9"),
            Date.valueOf("2018-12-9"),
            "test",
            (short) 1
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
        ExtraBudget unmanagedBudget = new ExtraBudget(
            unmanagedUser,
            "Madrid trip",
            new BigDecimal(0),
            new BigDecimal(100),
            Date.valueOf("2018-12-9"),
            Date.valueOf("2023-12-9"),
            "test",
            (short) 1
        );

        Assertions.assertThat(unmanagedBudget.getStartingDate()).isEqualTo(Date.valueOf("2023-12-9"));
    }

    @Test
    void testGettingCategoryOfMadridTripExpenseReturnsTravel() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        ExtraBudget unmanagedBudget = new ExtraBudget(
            unmanagedUser,
            "Madrid trip",
            new BigDecimal(0),
            new BigDecimal(100),
            Date.valueOf("2018-12-9"),
            Date.valueOf("2023-12-9"),
            "Travel",
            (short) 1
        );

        Assertions.assertThat(unmanagedBudget.getCategory()).isEqualTo("Travel");
    }

    @Test
    void testGettingIconofMadridTripExpenseReturnsShortOne() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        ExtraBudget unmanagedBudget = new ExtraBudget(
            unmanagedUser,
            "Madrid trip",
            new BigDecimal(0),
            new BigDecimal(100),
            Date.valueOf("2018-12-9"),
            Date.valueOf("2023-12-9"),
            "test",
            (short) 1
        );

        Assertions.assertThat(unmanagedBudget.getIconId()).isEqualTo((short) 1);
    }

    @Test
    void testUpdatingJustTheAmountModifiesOnlyTheAmount() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        ExtraBudget unmanagedBudget = new ExtraBudget(
            unmanagedUser,
            "Madrid trip",
            new BigDecimal(0),
            new BigDecimal(100),
            Date.valueOf("2018-12-9"),
            Date.valueOf("2023-12-9"),
            "test",
            (short) 1
        );
       unmanagedBudget.updateFrom(
                new BudgetEditingRequest(
                        null,
                        new BigDecimal(99),
                        null,
                        null,
                        null,
                        null,
                        null
                ),
                unmanagedUser
        );
        Assertions.assertThat(unmanagedBudget.getLimitAmount()).isEqualTo((BigDecimal.valueOf(99)));
    }

    @Test
    void testUpdatingJustTheConceptModifiesOnlyTheConcept() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        ExtraBudget unmanagedBudget = new ExtraBudget(
            unmanagedUser,
            "Madrid trip",
            new BigDecimal(0),
            new BigDecimal(100),
            Date.valueOf("2018-12-9"),
            Date.valueOf("2023-12-9"),
            "test",
            (short) 1
        );
       unmanagedBudget.updateFrom(
                new BudgetEditingRequest(  
                    "Paris Trip",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                ),
                unmanagedUser
        );
        Assertions.assertThat(unmanagedBudget.getName()).isEqualTo(("Paris Trip"));
    }

    @Test
    void testUpdatingCategoryAndIconIDModifiesThoseFields() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        ExtraBudget unmanagedBudget = new ExtraBudget(
            unmanagedUser,
            "Madrid trip",
            new BigDecimal(0),
            new BigDecimal(100),
            Date.valueOf("2018-12-9"),
            Date.valueOf("2023-12-9"),
            "test",
            (short) 1
        );
       unmanagedBudget.updateFrom(
                new BudgetEditingRequest(
                        null,
                        null,
                        null,
                        null,
                        null,
                        "Travel",
                        (short) 2
                ),
                unmanagedUser
        );
        Assertions.assertThat(unmanagedBudget.getCategory()).isEqualTo("Travel");
        Assertions.assertThat(unmanagedBudget.getIconId()).isEqualTo((short) 2);
    }

    @Test
    void testExtraExpenseCanBeCreatedFromDTO() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
       ExtraBudget unmanagedBudget = ExtraBudget.from(new BudgetAddingRequest(
                        "Paris Trip",
                        new BigDecimal(100),
                        Date.valueOf("2018-12-9"),
                        Date.valueOf("2023-12-9"),
                        "Travel",
                        (short) 2
                        )
                    , unmanagedUser
                    );
        Assertions.assertThat(unmanagedBudget.getLimitAmount()).isEqualTo((BigDecimal.valueOf(100)));
    }
    

    @Test
    void testExtraExpenseCanBeCastedToDTO() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );
        BudgetAddingRequest unamanagedRequest = new BudgetAddingRequest(
                        "Paris Trip",
                        new BigDecimal(100),
                        Date.valueOf("2018-12-9"),
                        Date.valueOf("2023-12-9"),
                        "Travel",
                        (short) 2
        );
       ExtraBudget unmanagedExpense = ExtraBudget.from(
                unamanagedRequest,
                unmanagedUser
        );
        Assertions.assertThat(unmanagedExpense.asDto()).isInstanceOf(BudgetDTO.class);
    }
    


}
