package org.mojodojocasahouse.extra.tests.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.model.ExtraExpense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraExpenseRepository;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Optional;


@DataJpaTest
class ExtraExpenseRepositoryTest {

    @Autowired
    private ExtraExpenseRepository repo;
    @Autowired
    private ExtraUserRepository userRepo;

    @Test
    void testFindingAnExpenseByConceptReturnsOne() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                        "Some",
                        "User",
                        "mj@me.com",
                        "a_password"
                );
        userRepo.save(user);
        ExtraExpense savedExpense = repo.save(
                new ExtraExpense(
                    user,
                    "concept",
                    new BigDecimal(100),
                    Date.valueOf("2018-12-9"),
                    "test",
                    (short) 1
                )
        );

        // execute
        Optional<ExtraExpense> foundExpense = repo.findByConcept("concept");

        // verify
        Assertions.assertThat(foundExpense).isEqualTo(Optional.of(savedExpense));
    }
    @Test
    void testFindingAnExpenseByCategoryReturnsOne() {
        // Setup - data
        ExtraUser user = new ExtraUser(
                        "Some",
                        "User",
                        "mj@me.com",
                        "a_password"
                );
        userRepo.save(user);
        ExtraExpense savedExpense = repo.save(
                new ExtraExpense(
                    user,
                    "concept",
                    new BigDecimal(100),
                    Date.valueOf("2018-12-9"),
                    "test",
                    (short) 1
                )
        );

        // execute
        Optional<ExtraExpense> foundExpense = repo.findByCategory("test");

        // verify
        Assertions.assertThat(foundExpense).isEqualTo(Optional.of(savedExpense));
    }

}