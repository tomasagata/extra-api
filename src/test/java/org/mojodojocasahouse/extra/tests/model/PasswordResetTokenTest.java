package org.mojodojocasahouse.extra.tests.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.exception.InvalidPasswordResetTokenException;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.model.PasswordResetToken;
import org.mojodojocasahouse.extra.testmodels.TestPasswordResetToken;

import java.sql.Timestamp;
import java.util.UUID;

public class PasswordResetTokenTest {

    @Test
    public void testGettingIdReturnsNull() {
        PasswordResetToken token = new TestPasswordResetToken(UUID.fromString("153b37f4-a2ed-4f0c-a7b2-7492a058c040"), null, 20);

        Assertions.assertThat(token.getId().toString()).isEqualTo("153b37f4-a2ed-4f0c-a7b2-7492a058c040");
    }

    @Test
    public void testGettingUserReturnsExtraUser() {
        ExtraUser user = new ExtraUser(
                "name",
                "lastname",
                "email@example.org",
                "Somepass"
        );
        PasswordResetToken token = new TestPasswordResetToken(UUID.randomUUID(), user, 20);

        Assertions.assertThat(token.getUser()).isEqualTo(user);
    }

    @Test
    public void testGettingValidUntilReturnsTimestamp() {
        ExtraUser user = new ExtraUser(
                "name",
                "lastname",
                "email@example.org",
                "Somepass"
        );
        PasswordResetToken token = new TestPasswordResetToken(UUID.randomUUID(), user, 20);

        Assertions.assertThat(token.getValidUntil()).isInstanceOf(Timestamp.class);
    }

    @Test
    public void testAssertingValidityOfPasswordResetTokenThrowsNothing() {
        ExtraUser user = new ExtraUser(
                "name",
                "lastname",
                "email@example.org",
                "Somepass"
        );
        PasswordResetToken token = new PasswordResetToken(user);

        Assertions.assertThatNoException().isThrownBy(token::assertValid);
    }

    @Test
    public void testAssertingValidityOfInvalidPasswordResetTokenThrowsNothing() {
        ExtraUser user = new ExtraUser(
                "name",
                "lastname",
                "email@example.org",
                "Somepass"
        );
        PasswordResetToken token = new TestPasswordResetToken(UUID.randomUUID(), user, -20);

        Assertions.assertThatException().isThrownBy(token::assertValid).isInstanceOf(InvalidPasswordResetTokenException.class);
    }

    @Test
    public void testAssertingValidityOfInvalidIdPasswordResetTokenThrowsNothing() {
        ExtraUser user = new ExtraUser(
                "name",
                "lastname",
                "email@example.org",
                "Somepass"
        );
        PasswordResetToken token = new TestPasswordResetToken(null, user, 20);

        Assertions.assertThatException().isThrownBy(token::assertValid).isInstanceOf(InvalidPasswordResetTokenException.class);
    }

}
