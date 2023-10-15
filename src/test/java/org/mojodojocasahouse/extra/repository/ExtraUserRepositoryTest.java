package org.mojodojocasahouse.extra.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;


@DataJpaTest
class ExtraUserRepositoryTest {

    @Autowired
    private ExtraUserRepository repo;

    @Test
    void testFindingAUserByEmailReturnsOne() {
        // Setup - data
        ExtraUser savedUser = repo.save(
                new ExtraUser(
                    "Some",
                    "User",
                    "mj@me.com",
                    "a_password"
                )
        );

        // execute
        Optional<ExtraUser> foundUser = repo.findByEmail("mj@me.com");

        // verify
        Assertions.assertThat(foundUser).isEqualTo(Optional.of(savedUser));
    }

    @Test
    void testSavingAUserWithANonUniqueEmailAddressThrowsDataIntegrityViolationException() {
        // Setup - data
        ExtraUser firstSavedUser = repo.save(
                new ExtraUser(
                        "Some",
                        "User",
                        "mj@me.com",
                        "a_password"
                )
        );
        ExtraUser userWithExistingEmail = new ExtraUser(
                    "Michael",
                    "Jackson",
                    "mj@me.com",
                    "another_password"
        );


        // execute and verify
        Assertions
                .assertThatThrownBy(() -> repo.save(userWithExistingEmail))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void testFindingAUserByEmailAndPasswordIsSuccessfulWhenBothFieldsAreCorrect() {
        // Setup - data
        ExtraUser savedUser = repo.save(
                new ExtraUser(
                        "Some",
                        "User",
                        "mj@me.com",
                        "a_password"
                )
        );


        // execute and verify
        Assertions.assertThat(repo.findOneByEmailAndPassword("mj@me.com", "another_Password")).isEmpty();
        Assertions.assertThat(repo.findOneByEmailAndPassword("AnotherEmail@Me.com", "a_password")).isEmpty();
        Assertions.assertThat(repo.findOneByEmailAndPassword("mj@me.com", "a_password")).isEqualTo(Optional.of(savedUser));
    }
}