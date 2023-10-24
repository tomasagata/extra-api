package org.mojodojocasahouse.extra.tests.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;


@DataJpaTest
class ExtraUserRepositoryTest {

    @Autowired
    private ExtraUserRepository userRepo;



    @Test
    void testFindingAUserByEmailReturnsOne() {
        // Setup - data
        ExtraUser savedUser = userRepo.save(
                new ExtraUser(
                    "Some",
                    "User",
                    "mj@me.com",
                    "a_password"
                )
        );

        // execute
        Optional<ExtraUser> foundUser = userRepo.findByEmail("mj@me.com");

        // verify
        Assertions.assertThat(foundUser).isEqualTo(Optional.of(savedUser));
    }

    @Test
    void testSavingAUserWithANonUniqueEmailAddressThrowsDataIntegrityViolationException() {
        // Setup - data
        userRepo.save(
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
                .assertThatThrownBy(() -> userRepo.save(userWithExistingEmail))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void testFindingAUserByEmailAndPasswordIsSuccessfulWhenBothFieldsAreCorrect() {
        // Setup - data
        ExtraUser savedUser = userRepo.save(
                new ExtraUser(
                        "Some",
                        "User",
                        "mj@me.com",
                        "a_password"
                )
        );


        // execute and verify
        Assertions.assertThat(userRepo.findOneByEmailAndPassword("mj@me.com", "another_Password")).isEmpty();
        Assertions.assertThat(userRepo.findOneByEmailAndPassword("AnotherEmail@Me.com", "a_password")).isEmpty();
        Assertions.assertThat(userRepo.findOneByEmailAndPassword("mj@me.com", "a_password")).isEqualTo(Optional.of(savedUser));
    }
}