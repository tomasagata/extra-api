package org.mojodojocasahouse.extra.tests.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.model.Category;
import org.mojodojocasahouse.extra.model.Expense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExpenseRepository;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Optional;


@DataJpaTest
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository repo;
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
        Expense savedExpense = repo.save(
                new Expense(
                        user,
                        "concept",
                        new BigDecimal(100),
                        Date.valueOf("2018-12-9"),
                        new Category("test", (short) 1, user)
                )
        );

        // execute
        Optional<Expense> foundExpense = repo.findFirstByConcept("concept");

        // verify
        Assertions.assertThat(foundExpense).isEqualTo(Optional.of(savedExpense));
    }

}